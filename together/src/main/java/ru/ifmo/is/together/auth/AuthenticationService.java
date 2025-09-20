package ru.ifmo.is.together.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.ifmo.is.together.auth.dto.AuthenticationDto;
import ru.ifmo.is.together.auth.dto.SignInDto;
import ru.ifmo.is.together.auth.dto.SignUpDto;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.errors.TokenExpiredException;
import ru.ifmo.is.together.common.errors.TooManyRequests;
import ru.ifmo.is.together.common.errors.UserAlreadyConfirmedException;
import ru.ifmo.is.together.passwordreset.PasswordResetToken;
import ru.ifmo.is.together.passwordreset.PasswordResetTokenService;
import ru.ifmo.is.together.passwordreset.dto.RequestPasswordResetDto;
import ru.ifmo.is.together.passwordreset.dto.ResetPasswordDto;
import ru.ifmo.is.together.refreshtokens.RefreshToken;
import ru.ifmo.is.together.refreshtokens.RefreshTokenService;
import ru.ifmo.is.together.refreshtokens.dto.RefreshDto;
import ru.ifmo.is.together.users.User;
import ru.ifmo.is.together.users.UserMapper;
import ru.ifmo.is.together.users.UserService;
import ru.ifmo.is.together.verificationtokens.VerificationToken;
import ru.ifmo.is.together.verificationtokens.VerificationTokenService;
import ru.ifmo.is.together.verificationtokens.dto.VerificationDto;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserService userService;
  private final UserMapper mapper;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  private final RefreshTokenService refreshService;
  private final VerificationTokenService verificationService;
  private final PasswordResetTokenService passwordResetService;

  /**
   * Получение текущего пользователя
   *
   * @return токен и текущий пользователь
   */
  public AuthenticationDto me() {
    var user = userService.getCurrentUser();

    var refreshToken = refreshService
      .findLast(user)
      .map(RefreshToken::getToken)
      .orElse(refreshService.createRefreshToken(user.getId()).getToken());

    var jwt = jwtService.generateToken(user);
    return new AuthenticationDto(jwt, refreshToken, mapper.map(user));
  }

  /**
   * Регистрация пользователя
   *
   * @param request данные пользователя
   * @return токен
   */
  @Transactional
  public AuthenticationDto signUp(SignUpDto request) {
    var user = User.builder()
      .username(request.getUsername())
      .email(request.getEmail())
      .roles(new HashSet<>())
      .password(passwordEncoder.encode(request.getPassword()))
      .build();

    user = userService.create(user);
    var jwt = jwtService.generateToken(user);
    var refreshToken = refreshService.createRefreshToken(user.getId());
    return new AuthenticationDto(jwt, refreshToken.getToken(), mapper.map(user));
  }

  /**
   * Аутентификация пользователя
   *
   * @param request данные пользователя
   * @return токен
   */
  @Transactional
  public AuthenticationDto signIn(SignInDto request) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
      request.getUsername(),
      request.getPassword()
    ));

    var userDetails = userService
      .userDetailsService()
      .loadUserByUsername(request.getUsername());

    var user = userService.getByUsername(userDetails.getUsername());

    var jwt = jwtService.generateToken(userDetails);
    var refreshToken = refreshService.createRefreshToken(user.getId());
    return new AuthenticationDto(jwt, refreshToken.getToken(), mapper.map(user));
  }

  /**
   * Обновление токенов доступа пользователя
   *
   * @param request refresh token
   * @return новая пара токенов
   */
  @Transactional
  public AuthenticationDto refresh(RefreshDto request) {
    return refreshService.findByToken(request.getRefreshToken())
      .map(refreshService::verifyExpiration)
      .map(RefreshToken::getUser)
      .map(user -> {
        var accessToken = jwtService.generateToken(user);
        var newRefreshToken = refreshService.createRefreshToken(user.getId());
        return new AuthenticationDto(accessToken, newRefreshToken.getToken(), mapper.map(user));
      })
      .orElseThrow(() -> new TokenExpiredException("No such refresh token"));
  }

  /**
   * Подтверждение профиля пользователя
   *
   * @param request verification token
   * @return токен
   */
  @Transactional
  public AuthenticationDto confirm(VerificationDto request) {
    return verificationService.findByToken(request.getVerificationToken())
      .map(verificationService::verifyExpiration)
      .map(VerificationToken::getUser)
      .map(u -> {
        var user = userService.confirm(u);
        verificationService.deleteByUser(user);

        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshService.createRefreshToken(user.getId());
        return new AuthenticationDto(accessToken, refreshToken.getToken(), mapper.map(user));
      })
      .orElseThrow(() -> new TokenExpiredException("No such verification token"));
  }

  /**
   * Изменение пароля пользователя
   *
   * @param request password reset token
   * @return токен
   */
  @Transactional
  public AuthenticationDto resetPassword(ResetPasswordDto request) {
    return passwordResetService.findByToken(request.getPasswordResetToken())
      .map(passwordResetService::verifyExpiration)
      .map(PasswordResetToken::getUser)
      .map(u -> {
        u.setPassword(passwordEncoder.encode(request.getPassword()));
        var user = userService.save(u);
        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshService.createRefreshToken(user.getId());
        return new AuthenticationDto(accessToken, refreshToken.getToken(), mapper.map(user));
      })
      .orElseThrow(() -> new TokenExpiredException("No such password reset token"));
  }

  /**
   * Повторная отправка письма для подтверждения профиля пользователя
   */
  @Transactional
  public void resendConfirmation() {
    var user = userService.getCurrentUser();

    if (user.isUser()) {
      throw new UserAlreadyConfirmedException("You are already a user. You have nothing to verify");
    }

    var lastVerificationToken = verificationService.findLastVerificationToken(user);
    if (lastVerificationToken.isEmpty()) {
      throw new UserAlreadyConfirmedException("There are no unconfirmed mails. You have nothing to verify");
    }

    var secondsFromSent = Duration.between(lastVerificationToken.get().getSentAt(), Instant.now()).toSeconds();
    if (secondsFromSent < 60) {
      throw new TooManyRequests(
        "The time between two confirmation mail sendings must be at least 1 minute. Please, try after " + (60 - secondsFromSent) + " seconds"
      );
    }

    userService.sendConfirmation(user, true);
  }

  /**
   * Запрос на сброс пароля
   *
   * @param request данные пользователя (email)
   */
  @Transactional
  public void requestPasswordReset(RequestPasswordResetDto request) {
    var user = userService.findByEmail(request.getEmail())
      .orElseThrow(() -> new ResourceNotFoundException("User with email " + request.getEmail() +" not found"));

    var lastPasswordResetToken = passwordResetService.findLast(user);
    if (lastPasswordResetToken.isPresent()) {
      var secondsFromLastSent = Duration.between(lastPasswordResetToken.get().getDate(), Instant.now()).toSeconds();
      if (secondsFromLastSent < 60) {
        throw new TooManyRequests(
          "The time between two password reset requests must be at least 1 minute. Please, try after " + (60 - secondsFromLastSent) + " seconds"
        );
      }
    }

    userService.sendPasswordReset(user);
  }

  /**
   * Выйти из аккаунта
   */
  @Transactional
  public void signOut(boolean onAllDevices) {
    var user = userService.getCurrentUser();

    if (onAllDevices) {
      refreshService.deleteByUserId(user.getId());
    }
  }
}

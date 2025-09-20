package ru.ifmo.is.together.passwordreset;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ifmo.is.together.common.errors.TokenExpiredException;
import ru.ifmo.is.together.common.utils.crypto.TokenGenerator;
import ru.ifmo.is.together.users.User;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

  @Value("${app.auth.password_reset_token.ttl}")
  private String passwordResetTokenTtl;

  @Value("${app.auth.password_reset_token.length}")
  private String passwordResetTokenLength;

  private final PasswordResetTokenRepository repository;
  private final TokenGenerator tokenGenerator;

  public Optional<PasswordResetToken> findByToken(String token) {
    return repository.findByToken(token);
  }

  public Optional<PasswordResetToken> findLast(User user) {
    return repository.findTopByUserOrderByDateDesc(user);
  }

  public PasswordResetToken createPasswordResetToken(User user) {
    var token = tokenGenerator.generateSecureToken(Integer.parseInt(passwordResetTokenLength));
    var passwordResetToken = PasswordResetToken.builder()
      .user(user)
      .date(Instant.now())
      .token(token)
      .build();

    return repository.save(passwordResetToken);
  }

  public PasswordResetToken verifyExpiration(PasswordResetToken token) {
    if (token.getDate().plusMillis(Long.parseLong(passwordResetTokenTtl)).compareTo(Instant.now()) < 0) {
      repository.delete(token);
      throw new TokenExpiredException("Password reset token was expired. Please make a new password reset request");
    }

    return token;
  }
}

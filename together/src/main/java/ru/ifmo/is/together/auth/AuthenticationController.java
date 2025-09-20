package ru.ifmo.is.together.auth;

import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.ifmo.is.together.auth.dto.AuthenticationDto;
import ru.ifmo.is.together.auth.dto.SignInDto;
import ru.ifmo.is.together.auth.dto.SignUpDto;
import ru.ifmo.is.together.passwordreset.dto.RequestPasswordResetDto;
import ru.ifmo.is.together.passwordreset.dto.ResetPasswordDto;
import ru.ifmo.is.together.refreshtokens.dto.RefreshDto;
import ru.ifmo.is.together.common.context.ApplicationLockBean;
import ru.ifmo.is.together.verificationtokens.dto.VerificationDto;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {
  private final ApplicationLockBean applicationLockBean;
  private final AuthenticationService authenticationService;

  @Operation(summary = "Получение текущего пользователя", security = @SecurityRequirement(name = "bearerAuth"))
  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<AuthenticationDto> me() {
    return ResponseEntity.ok(authenticationService.me());
  }

  @Operation(summary = "Регистрация пользователя")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/sign-up")
  public ResponseEntity<AuthenticationDto> signUp(@RequestBody @Valid SignUpDto request) {
    applicationLockBean.getImportLock().lock();
    try {
      return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.signUp(request));
    } finally {
      applicationLockBean.getImportLock().unlock();
    }
  }

  @Operation(summary = "Авторизация пользователя")
  @PostMapping("/sign-in")
  public ResponseEntity<AuthenticationDto> signIn(@RequestBody @Valid SignInDto request) {
    return ResponseEntity.ok(authenticationService.signIn(request));
  }

  @Operation(summary = "Обновление токена доступа")
  @PostMapping("/refresh")
  public ResponseEntity<AuthenticationDto> refresh(@RequestBody @Valid RefreshDto request) {
    return ResponseEntity.ok(authenticationService.refresh(request));
  }

  @Operation(summary = "Подтверждение электронной почты")
  @PostMapping("/confirm")
  public ResponseEntity<AuthenticationDto> confirm(@RequestBody @Valid VerificationDto request) {
    return ResponseEntity.ok(authenticationService.confirm(request));
  }

  @Operation(summary = "Запрос на сброс пароля")
  @PostMapping("/request-password-reset")
  public ResponseEntity<Void> requestPasswordReset(@RequestBody @Valid RequestPasswordResetDto request) {
    authenticationService.requestPasswordReset(request);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Сброс пароля")
  @PostMapping("/reset-password")
  public ResponseEntity<AuthenticationDto> resetPassword(@RequestBody @Valid ResetPasswordDto request) {
    return ResponseEntity.ok(authenticationService.resetPassword(request));
  }

  @Operation(summary = "Повторно отправить письмо с подтверждением", security = @SecurityRequirement(name = "bearerAuth"))
  @GetMapping("/resend-confirmation")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<AuthenticationDto> resendConfirmation() {
    authenticationService.resendConfirmation();
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Выйти из аккаунта", security = @SecurityRequirement(name = "bearerAuth"))
  @DeleteMapping("/sign-out")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> signOut(@RequestParam(defaultValue = "false") boolean onAllDevices) {
    authenticationService.signOut(onAllDevices);
    return ResponseEntity.noContent().build();
  }
}

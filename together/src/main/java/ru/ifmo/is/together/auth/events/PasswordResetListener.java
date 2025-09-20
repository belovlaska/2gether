package ru.ifmo.is.together.auth.events;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.ifmo.is.together.passwordreset.PasswordResetTokenService;
import ru.ifmo.is.together.users.UserMailer;

@Component
@RequiredArgsConstructor
public class PasswordResetListener implements
  ApplicationListener<OnPasswordResetRequestEvent> {

  private final UserMailer userMailer;
  private final PasswordResetTokenService tokenService;

  @Override
  public void onApplicationEvent(@NotNull OnPasswordResetRequestEvent event) {
    this.sendPasswordResetInstructions(event);
  }

  private void sendPasswordResetInstructions(OnPasswordResetRequestEvent event) {
    var user = event.getUser();
    var token = tokenService.createPasswordResetToken(user);
    userMailer.sendPasswordResetInstructions(user, event.getLocale(), token);
  }
}

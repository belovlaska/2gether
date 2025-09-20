package ru.ifmo.is.together.auth.events;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;
import ru.ifmo.is.together.users.User;

import java.util.Locale;

@Getter
@Setter
@Builder
@ToString
public class OnPasswordResetRequestEvent extends ApplicationEvent {
  private final User user;
  private final Locale locale;

  public OnPasswordResetRequestEvent(User user, Locale locale) {
    super(user);
    this.user = user;
    this.locale = locale;
  }
}

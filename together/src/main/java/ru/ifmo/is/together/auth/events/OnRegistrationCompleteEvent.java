package ru.ifmo.is.together.auth.events;

import lombok.*;
import org.springframework.context.ApplicationEvent;
import ru.ifmo.is.together.users.User;

import java.util.Locale;

@Getter
@Setter
@Builder
@ToString
public class OnRegistrationCompleteEvent extends ApplicationEvent {
  private final User user;
  private final boolean newUser;
  private final Locale locale;

  public OnRegistrationCompleteEvent(User user, boolean newUser, Locale locale) {
    super(user);
    this.user = user;
    this.newUser = newUser;
    this.locale = locale;
  }
}

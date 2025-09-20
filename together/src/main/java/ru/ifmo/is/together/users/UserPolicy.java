package ru.ifmo.is.together.users;

import org.springframework.stereotype.Component;
import ru.ifmo.is.together.common.framework.CrudPolicy;

@Component
public class UserPolicy extends CrudPolicy<User> {

  @Override
  public boolean canUpdate(User user, User object) {
    return user.equals(object) || user.isAdmin();
  }

  @Override
  public User getCreator(User user) {
    return user;
  }

  @Override
  public String getPolicySubject() {
    return "users";
  }
}

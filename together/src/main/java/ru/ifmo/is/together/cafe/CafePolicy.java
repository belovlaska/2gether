package ru.ifmo.is.together.cafe;

import org.springframework.stereotype.Component;
import ru.ifmo.is.together.common.framework.CrudPolicy;
import ru.ifmo.is.together.users.User;

@Component
public class CafePolicy extends CrudPolicy<Cafe> {
  @Override
  public boolean canCreate(User user) {
    return user.isAdmin();
  }

  @Override
  public boolean canDelete(User user, Cafe object) {
    return user.isAdmin() || object.getOwner().equals(user);
  }

  @Override
  public boolean canUpdate(User user, Cafe object) {
    return user.isAdmin() || object.getOwner().equals(user);
  }

  @Override
  public User getCreator(Cafe object) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getPolicySubject() {
    return "cafes";
  }
}

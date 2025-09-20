package ru.ifmo.is.together.common.framework;

import org.springframework.stereotype.Component;
import ru.ifmo.is.together.common.policies.Policy;
import ru.ifmo.is.together.users.User;

@Component
public abstract class CrudPolicy<T extends CrudEntity> extends Policy<T> {

  @Override
  public boolean canShowAll(User user) {
    return true;
  }

  @Override
  public boolean canSearch(User user) {
    return true;
  }

  @Override
  public boolean canShow(User user, T object) {
    return true;
  }

  @Override
  public boolean canCreate(User user) {
    return true;
  }

  @Override
  public boolean canDelete(User user, T object) {
    return canManage(user, object);
  }

  @Override
  public boolean canUpdate(User user, T object) {
    return canManage(user, object);
  }

  protected boolean canManage(User user, T object) {
    return user != null && (user.equals(getCreator(object)) || user.isAdmin());
  }

  public abstract User getCreator(T object);
}

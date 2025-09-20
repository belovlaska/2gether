package ru.ifmo.is.together.common.policies;

import ru.ifmo.is.together.common.errors.PolicyViolationError;
import ru.ifmo.is.together.users.User;

abstract public class Policy<T> {

  public boolean can(ActionType actionType, User user, T object) {
    return switch (actionType) {
      case SHOW -> canShow(user, object);
      case SHOW_ALL -> canShowAll(user);
      case SEARCH -> canSearch(user);
      case CREATE -> canCreate(user);
      case DELETE -> canDelete(user, object);
      case UPDATE -> canUpdate(user, object);
      case MANAGE -> canCreate(user) && canUpdate(user, object) && canDelete(user, object);
    };
  }

  abstract public boolean canShowAll(User user);

  public void showAll(User user) {
    if (canShowAll(user)) return;
    throw constructViolationError(user, "view all");
  }

  abstract public boolean canSearch(User user);

  public void search(User user) {
    if (canSearch(user)) return;
    throw constructViolationError(user, "search and filter data");
  }

  abstract public boolean canShow(User user, T object);

  public void show(User user, T object) {
    if (canShow(user, object)) return;
    throw constructViolationError(user, "view");
  }

  abstract public boolean canCreate(User user);

  public void create(User user) {
    if (canCreate(user)) return;
    throw constructViolationError(user, "create");
  }

  abstract public boolean canDelete(User user, T object);

  public void delete(User user, T object) {
    if (canDelete(user, object)) return;
    throw constructViolationError(user, "delete");
  }

  abstract public boolean canUpdate(User user, T object);

  public void update(User user, T object) {
    if (canUpdate(user, object)) return;
    throw constructViolationError(user, "update");
  }

  abstract public String getPolicySubject();

  private PolicyViolationError constructViolationError(User user, String action) {
    return new PolicyViolationError("User#" + user.getId() + " cant " + action + " " + getPolicySubject() + ".");
  }
}

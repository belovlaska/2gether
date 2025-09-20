package ru.ifmo.is.together.userroles;

import org.springframework.stereotype.Component;
import ru.ifmo.is.together.common.framework.CrudPolicy;
import ru.ifmo.is.together.users.User;

@Component
public class UserRolePolicy extends CrudPolicy<User> {

  @Override
  public boolean canUpdate(User user, User object) {
    // Пользователь не может добавить себе роль
    return user.getId() != object.getId()
      // Администратор может добавлять любому любую роль
      && user.isAdmin();
  }

  @Override
  public boolean canDelete(User user, User object) {
    // Пользователь не может удалить себе роль
    return user.getId() != object.getId()
      // Никто не может удалить роль у администратора
      && !object.isAdmin()
      // Администратор может удалить роль любому кроме другого администратора
      && user.isAdmin();
  }

  @Override
  public User getCreator(User user) {
    throw new UnsupportedOperationException("This method is not used anywhere!");
  }

  @Override
  public String getPolicySubject() {
    return "user-roles";
  }
}

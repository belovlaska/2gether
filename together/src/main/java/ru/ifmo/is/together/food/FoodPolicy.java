package ru.ifmo.is.together.food;

import org.springframework.stereotype.Component;
import ru.ifmo.is.together.common.framework.CrudPolicy;
import ru.ifmo.is.together.users.User;


@Component
public class FoodPolicy extends CrudPolicy<Food> {

  @Override
  public User getCreator(Food object) {
    return object.getCafe().getOwner();
  }

  @Override
  public String getPolicySubject() {
    return "food";
  }
}

package ru.ifmo.is.together.drink;

import org.springframework.stereotype.Component;
import ru.ifmo.is.together.common.framework.CrudPolicy;
import ru.ifmo.is.together.food.Food;
import ru.ifmo.is.together.users.User;

@Component
public class DrinkPolicy extends CrudPolicy<Drink> {

  @Override
  public User getCreator(Drink object) {
    return object.getCafe().getOwner();
  }

  @Override
  public String getPolicySubject() {
    return "drink";
  }
}

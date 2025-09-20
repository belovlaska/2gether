package ru.ifmo.is.together.game;

import org.springframework.stereotype.Component;
import ru.ifmo.is.together.common.framework.CrudPolicy;
import ru.ifmo.is.together.food.Food;
import ru.ifmo.is.together.users.User;

@Component
public class GamePolicy extends CrudPolicy<Game> {

  @Override
  public User getCreator(Game object) {
    return object.getCafe().getOwner();
  }

  @Override
  public String getPolicySubject() {
    return "game";
  }
}

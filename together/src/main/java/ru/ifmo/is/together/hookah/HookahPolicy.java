package ru.ifmo.is.together.hookah;

import org.springframework.stereotype.Component;
import ru.ifmo.is.together.common.framework.CrudPolicy;

import ru.ifmo.is.together.users.User;


@Component
public class HookahPolicy extends CrudPolicy<Hookah> {

  @Override
  public User getCreator(Hookah object) {
    return object.getCafe().getOwner();
  }

  @Override
  public String getPolicySubject() {
    return "hookah";
  }
}


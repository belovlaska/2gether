package ru.ifmo.is.together.lobby;

import org.springframework.stereotype.Component;
import ru.ifmo.is.together.common.framework.CrudPolicy;
import ru.ifmo.is.together.users.User;

@Component
public class LobbyPolicy extends CrudPolicy<Lobby> {


  @Override
  public String getPolicySubject() {
    return "lobbies";
  }

  @Override
  public User getCreator(Lobby object) {
    throw new UnsupportedOperationException();
  }
}

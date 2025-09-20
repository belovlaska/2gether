package ru.ifmo.is.together.lobby;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.ifmo.is.together.cafe.Cafe;

import ru.ifmo.is.together.users.User;

@Component
@RequiredArgsConstructor
public class LobbySpecification {

  public Specification<Lobby> hasUser(User user) {
    return (root, query, cb) -> {
      Join<Lobby, User> userJoin = root.join("participants", JoinType.INNER);
      return cb.equal(userJoin, user);
    };
  }

  public Specification<Lobby> inCafe(Cafe cafe) {
    return (root, query, cb) -> {
      Join<Lobby, Cafe> cafeJoin = root.join("cafe", JoinType.INNER);
      return cb.equal(cafeJoin, cafe);
    };
  }
}

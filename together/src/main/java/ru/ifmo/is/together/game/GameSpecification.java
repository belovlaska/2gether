package ru.ifmo.is.together.game;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.ifmo.is.together.common.application.VisibleSpecification;
import ru.ifmo.is.together.food.Food;

@Component
@RequiredArgsConstructor
public class GameSpecification extends VisibleSpecification<Game> {

  public Specification<Game> withCafe(int cafeId) {
    return with("cafe", cafeId);
  }
}

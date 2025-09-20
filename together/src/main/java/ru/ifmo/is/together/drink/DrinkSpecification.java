package ru.ifmo.is.together.drink;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.ifmo.is.together.common.application.VisibleSpecification;
import ru.ifmo.is.together.food.Food;

@Component
@RequiredArgsConstructor
public class DrinkSpecification extends VisibleSpecification<Drink> {

  public Specification<Drink> withCafe(int cafeId) {
    return with("cafe", cafeId);
  }
}

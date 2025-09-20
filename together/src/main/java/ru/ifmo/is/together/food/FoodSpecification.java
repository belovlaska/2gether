package ru.ifmo.is.together.food;



import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.ifmo.is.together.common.application.VisibleSpecification;


@Component
@RequiredArgsConstructor
public class FoodSpecification extends VisibleSpecification<Food> {

  public Specification<Food> withCafe(int cafeId) {
    return with("cafe", cafeId);
  }
}

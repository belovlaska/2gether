package ru.ifmo.is.together.hookah;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.ifmo.is.together.common.application.VisibleSpecification;

@Component
@RequiredArgsConstructor
public class HookahSpecification extends VisibleSpecification<Hookah> {

  public Specification<Hookah> withCafe(int cafeId) {
    return with("cafe", cafeId);
  }
}


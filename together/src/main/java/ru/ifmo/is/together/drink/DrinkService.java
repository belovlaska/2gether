package ru.ifmo.is.together.drink;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.common.application.ApplicationService;
import ru.ifmo.is.together.common.errors.PolicyViolationError;
import ru.ifmo.is.together.common.errors.ResourceAlreadyExists;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.search.SearchDto;
import ru.ifmo.is.together.common.search.SearchMapper;


import ru.ifmo.is.together.drink.dto.*;


import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DrinkService extends ApplicationService {

  private final DrinkMapper mapper;
  private final DrinkPolicy policy;
  private final DrinkRepository repository;
  private final DrinkSpecification specification;
  private final SearchMapper<Drink> searchMapper;

  public Page<DrinkDto> getCafeDrink(Cafe cafe, Pageable pageable) {
    policy.showAll(currentUser());

    var spec = specification.withCafe(cafe.getId());

    var drinks = repository.findAll(spec, pageable);
    return drinks.map(mapper::map);
  }

  public Page<DrinkDto> findBySearchCriteria(SearchDto searchData,Cafe cafe, Pageable pageable) {
    policy.search(currentUser());

    var spec = searchMapper.map(searchData).and(specification.withCafe(cafe.getId()));

    var drinks = repository.findAll(spec, pageable);
    return drinks.map(mapper::map);
  }



  @Transactional
  public DrinkDto create(DrinkCreateDto dto, Cafe cafe) {
    policy.create(currentUser());

    if(cafe.getOwner().equals(currentUser())) {
      var drink = mapper.map(dto);
      drink.setCafe(cafe);
      repository.save(drink);
      return mapper.map(drink);
    }
    else {
      throw new AccessDeniedException("Only owner can create drinks");

    }
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public boolean delete(int id) {
    var drink = repository.findById(id);
    return drink.map(f -> {
      policy.delete(currentUser(), f);

      repository.delete(f);
      return true;
    }).orElse(false);
  }
}


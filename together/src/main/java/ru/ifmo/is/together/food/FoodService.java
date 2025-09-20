package ru.ifmo.is.together.food;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.cafe.dto.CafeDto;
import ru.ifmo.is.together.common.application.ApplicationService;
import ru.ifmo.is.together.common.errors.PolicyViolationError;
import ru.ifmo.is.together.common.errors.ResourceAlreadyExists;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.search.SearchDto;
import ru.ifmo.is.together.common.search.SearchMapper;
import ru.ifmo.is.together.food.dto.*;
import ru.ifmo.is.together.users.User;
import ru.ifmo.is.together.users.dto.UserDto;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FoodService extends ApplicationService {

  private final FoodMapper mapper;
  private final FoodPolicy policy;
  private final FoodRepository repository;
  private final FoodSpecification specification;
  private final SearchMapper<Food> searchMapper;


  public Page<FoodDto> getCafeFood(Cafe cafe, Pageable pageable) {
    policy.showAll(currentUser());

    // Everyone can see all cafe's food
    var spec = specification.withCafe(cafe.getId());

    var food = repository.findAll(spec, pageable);
    return food.map(mapper::map);
  }


  //надо сделать чтобы смотрел только по 1 кафе
  public Page<FoodDto> findBySearchCriteriaAndCafe(SearchDto searchData, Cafe cafe, Pageable pageable) {
    policy.search(currentUser());

      var spec = searchMapper.map(searchData).and(specification.withCafe(cafe.getId()));


    var food = repository.findAll(spec, pageable);
    return food.map(mapper::map);
  }


  @Transactional
  public FoodDto create(FoodCreateDto dto, Cafe cafe) {
    policy.create(currentUser());

    if(cafe.getOwner().equals(currentUser())) {
      var food = mapper.map(dto);
      food.setCafe(cafe);
      repository.save(food);
      return mapper.map(food);
    }
    else {
      throw new AccessDeniedException("Only owner can create foods");

    }
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public boolean delete(int id) {
    var food = repository.findById(id);
    return food.map(f -> {
      policy.delete(currentUser(), f);

      repository.delete(f);
      return true;
    }).orElse(false);
  }

}

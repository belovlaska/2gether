package ru.ifmo.is.together.food;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.search.SearchDto;
import ru.ifmo.is.together.common.search.SearchMapper;
import ru.ifmo.is.together.common.services.BaseCafeEntityService;
import ru.ifmo.is.together.food.dto.*;

@Service
@RequiredArgsConstructor
public class FoodService extends BaseCafeEntityService<Food, FoodDto, FoodCreateDto, FoodUpdateDto, FoodRepository, FoodMapper, FoodPolicy, FoodSpecification> {

  public FoodService(FoodMapper mapper, FoodPolicy policy, FoodRepository repository, FoodSpecification specification, SearchMapper<Food> searchMapper) {
    super(mapper, policy, repository, specification, searchMapper);
  }

  @Override
  protected Food createEntityFromDto(FoodCreateDto dto) {
    return mapper.map(dto);
  }
}
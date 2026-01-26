package ru.ifmo.is.together.drink;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.search.SearchDto;
import ru.ifmo.is.together.common.search.SearchMapper;
import ru.ifmo.is.together.common.services.BaseCafeEntityService;
import ru.ifmo.is.together.drink.dto.*;

@Service
@RequiredArgsConstructor
public class DrinkService extends BaseCafeEntityService<Drink, DrinkDto, DrinkCreateDto, DrinkUpdateDto, DrinkRepository, DrinkMapper, DrinkPolicy, DrinkSpecification> {

  public DrinkService(DrinkMapper mapper, DrinkPolicy policy, DrinkRepository repository, DrinkSpecification specification, SearchMapper<Drink> searchMapper) {
    super(mapper, policy, repository, specification, searchMapper);
  }

  @Override
  protected Drink createEntityFromDto(DrinkCreateDto dto) {
    return mapper.map(dto);
  }
}
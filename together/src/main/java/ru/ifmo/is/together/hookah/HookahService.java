package ru.ifmo.is.together.hookah;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.search.SearchDto;
import ru.ifmo.is.together.common.search.SearchMapper;
import ru.ifmo.is.together.common.services.BaseCafeEntityService;
import ru.ifmo.is.together.hookah.dto.*;

@Service
@RequiredArgsConstructor
public class HookahService extends BaseCafeEntityService<Hookah, HookahDto, HookahCreateDto, HookahUpdateDto, HookahRepository, HookahMapper, HookahPolicy, HookahSpecification> {

  public HookahService(HookahMapper mapper, HookahPolicy policy, HookahRepository repository, HookahSpecification specification, SearchMapper<Hookah> searchMapper) {
    super(mapper, policy, repository, specification, searchMapper);
  }

  @Override
  protected Hookah createEntityFromDto(HookahCreateDto dto) {
    return mapper.map(dto);
  }
}
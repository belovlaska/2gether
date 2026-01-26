package ru.ifmo.is.together.game;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.search.SearchDto;
import ru.ifmo.is.together.common.search.SearchMapper;
import ru.ifmo.is.together.common.services.BaseCafeEntityService;
import ru.ifmo.is.together.game.dto.*;

@Service
@RequiredArgsConstructor
public class GameService extends BaseCafeEntityService<Game, GameDto, GameCreateDto, GameUpdateDto, GameRepository, GameMapper, GamePolicy, GameSpecification> {

  public GameService(GameMapper mapper, GamePolicy policy, GameRepository repository, GameSpecification specification, SearchMapper<Game> searchMapper) {
    super(mapper, policy, repository, specification, searchMapper);
  }

  @Override
  protected Game createEntityFromDto(GameCreateDto dto) {
    return mapper.map(dto);
  }
}


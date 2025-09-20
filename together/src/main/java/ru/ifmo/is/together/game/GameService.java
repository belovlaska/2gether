package ru.ifmo.is.together.game;


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
import ru.ifmo.is.together.food.*;
import ru.ifmo.is.together.food.dto.*;
import ru.ifmo.is.together.game.dto.GameCreateDto;
import ru.ifmo.is.together.game.dto.GameDto;
import ru.ifmo.is.together.users.User;
import ru.ifmo.is.together.users.dto.UserDto;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService extends ApplicationService {

  private final GameMapper mapper;
  private final GamePolicy policy;
  private final GameRepository repository;
  private final GameSpecification specification;
  private final SearchMapper<Game> searchMapper;


  public Page<GameDto> getCafeFood(Cafe cafe, Pageable pageable) {
    policy.showAll(currentUser());

    // Everyone can see all cafe's food
    var spec = specification.withCafe(cafe.getId());

    var game = repository.findAll(spec, pageable);
    return game.map(mapper::map);
  }


  //надо сделать чтобы смотрел только по 1 кафе
  public Page<GameDto> findBySearchCriteriaAndCafe(SearchDto searchData, Cafe cafe, Pageable pageable) {
    policy.search(currentUser());

    var spec = searchMapper.map(searchData).and(specification.withCafe(cafe.getId()));


    var game = repository.findAll(spec, pageable);
    return game.map(mapper::map);
  }


  @Transactional
  public GameDto create(GameCreateDto dto, Cafe cafe) {
    policy.create(currentUser());

    if(cafe.getOwner().equals(currentUser())) {
      var game = mapper.map(dto);
      game.setCafe(cafe);
      repository.save(game);
      return mapper.map(game);
    }
    else {
      throw new AccessDeniedException("Only owner can create games");

    }
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public boolean delete(int id) {
    var game = repository.findById(id);
    return game.map(g -> {
      policy.delete(currentUser(), g);

      repository.delete(g);
      return true;
    }).orElse(false);
  }

}


package ru.ifmo.is.together.hookah;



import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


import ru.ifmo.is.together.hookah.dto.*;
import ru.ifmo.is.together.users.User;

import org.springframework.security.access.AccessDeniedException;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HookahService extends ApplicationService {

  private final HookahMapper mapper;
  private final HookahPolicy policy;
  private final HookahRepository repository;
  private final HookahSpecification specification;
  private final SearchMapper<Hookah> searchMapper;



  public Page<HookahDto> getCafeHookah(Cafe cafe, Pageable pageable) {
    policy.showAll(currentUser());

    var spec = specification.withCafe(cafe.getId());

    var hookahs = repository.findAll(spec, pageable);
    return hookahs.map(mapper::map);
  }

  public Page<HookahDto> findBySearchCriteriaAndCafe(SearchDto searchData, Cafe cafe, Pageable pageable) {
    policy.search(currentUser());

    var spec = searchMapper.map(searchData).and(specification.withCafe(cafe.getId()));
    var hookahs = repository.findAll(spec, pageable);
    return hookahs.map(mapper::map);
  }




  @Transactional
  public HookahDto create(HookahCreateDto dto, Cafe cafe) {
    policy.create(currentUser());

    if(cafe.getOwner().equals(currentUser())) {
      var hookah = mapper.map(dto);
      hookah.setCafe(cafe);
      repository.save(hookah);
      return mapper.map(hookah);
    }
    else {
      throw new AccessDeniedException("Only owner can create hookahs");

    }
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public boolean delete(int id) {
    var hookah = repository.findById(id);
    return hookah.map(f -> {
      policy.delete(currentUser(), f);

      repository.delete(f);
      return true;
    }).orElse(false);
  }


}

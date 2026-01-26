package ru.ifmo.is.together.common.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.common.application.ApplicationService;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.policies.BaseEntityPolicy;
import ru.ifmo.is.together.common.mapper.BaseEntityMapper;
import ru.ifmo.is.together.common.entity.BaseEntity;
import ru.ifmo.is.together.common.search.SearchDto;
import ru.ifmo.is.together.common.search.SearchMapper;
import ru.ifmo.is.together.common.search.BaseSpecification;
import ru.ifmo.is.together.common.framework.CrudEntity;

public abstract class BaseCafeEntityService<
    E extends CrudEntity,
    D,
    CD,
    UD,
    R extends org.springframework.data.jpa.repository.JpaRepository<E, Integer>,
    M extends BaseEntityMapper<E, D, CD, UD>,
    P extends BaseEntityPolicy<E>,
    S extends BaseSpecification<E>
> extends ApplicationService {

    protected final M mapper;
    protected final P policy;
    protected final R repository;
    protected final S specification;
    protected final SearchMapper<E> searchMapper;

    public BaseCafeEntityService(M mapper, P policy, R repository, S specification, SearchMapper<E> searchMapper) {
        this.mapper = mapper;
        this.policy = policy;
        this.repository = repository;
        this.specification = specification;
        this.searchMapper = searchMapper;
    }

    public Page<D> getCafeEntities(Cafe cafe, Pageable pageable) {
        policy.showAll(currentUser());

        var spec = specification.withCafe(cafe.getId());

        var entities = repository.findAll(spec, pageable);
        return entities.map(mapper::map);
    }

    public Page<D> findBySearchCriteriaAndCafe(SearchDto searchData, Cafe cafe, Pageable pageable) {
        policy.search(currentUser());

        var spec = searchMapper.map(searchData).and(specification.withCafe(cafe.getId()));

        var entities = repository.findAll(spec, pageable);
        return entities.map(mapper::map);
    }

    @Transactional
    public D create(CD dto, Cafe cafe) {
        policy.create(currentUser());

        if (cafe.getOwner().equals(currentUser())) {
            var entity = createEntityFromDto(dto);
            entity.setCafe(cafe);
            repository.save(entity);
            return mapper.map(entity);
        } else {
            throw new AccessDeniedException("Only owner can create entities");
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean delete(int id) {
        var entityOpt = repository.findById(id);
        return entityOpt.map(entity -> {
            policy.delete(currentUser(), entity);

            repository.delete(entity);
            return true;
        }).orElse(false);
    }

    protected abstract E createEntityFromDto(CD dto);
}
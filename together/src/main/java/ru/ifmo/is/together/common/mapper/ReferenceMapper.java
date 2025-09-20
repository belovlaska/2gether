package ru.ifmo.is.together.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.TargetType;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ifmo.is.together.common.entity.BaseEntity;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedTargetPolicy = ReportingPolicy.WARN
)
public abstract class ReferenceMapper {
  @Autowired
  private EntityManager entityManager;

  public <T extends BaseEntity> T toEntity(Long id, @TargetType Class<T> entityClass) {
    return id != null ? entityManager.find(entityClass, id) : null;
  }
}

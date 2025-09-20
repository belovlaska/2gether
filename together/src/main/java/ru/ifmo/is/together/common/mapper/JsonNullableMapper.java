package ru.ifmo.is.together.common.mapper;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedTargetPolicy = ReportingPolicy.WARN
)
public abstract class JsonNullableMapper {

  public <T> JsonNullable<T> wrap(T entity) {
    return JsonNullable.of(entity);
  }

  public <T> T unwrap(JsonNullable<T> jsonNullable) {
    return jsonNullable == null ? null : jsonNullable.orElse(null);
  }

  /**
   * Checks whether nullable parameter was passed explicitly.
   *
   * @return true if value was set explicitly, false otherwise
   */
  @Condition
  public <T> boolean isPresent(JsonNullable<T> nullable) {
    return nullable != null && nullable.isPresent();
  }
}

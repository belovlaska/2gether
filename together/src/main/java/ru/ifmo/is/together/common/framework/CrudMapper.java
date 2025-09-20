package ru.ifmo.is.together.common.framework;

import org.mapstruct.*;
import ru.ifmo.is.together.common.framework.dto.CrudDto;

public interface CrudMapper<T extends CrudEntity, TDto extends CrudDto, TCreateDto, TUpdateDto> {
  T map(TCreateDto dto);
  TDto map(T model);
  T map(TDto dto);
  void update(TUpdateDto dto, @MappingTarget T model);
}

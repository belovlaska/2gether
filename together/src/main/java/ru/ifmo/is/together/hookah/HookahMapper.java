package ru.ifmo.is.together.hookah;

import org.mapstruct.*;
import ru.ifmo.is.together.cafe.CafeMapper;
import ru.ifmo.is.together.common.framework.CrudMapper;
import ru.ifmo.is.together.common.mapper.JsonNullableMapper;
import ru.ifmo.is.together.common.mapper.ReferenceMapper;
import ru.ifmo.is.together.hookah.dto.*;
import ru.ifmo.is.together.users.UserMapper;

@Mapper(
  uses = { JsonNullableMapper.class, ReferenceMapper.class, UserMapper.class, CafeMapper.class },
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class HookahMapper implements CrudMapper<Hookah, HookahDto, HookahCreateDto, Object> {
  public abstract Hookah map(HookahCreateDto dto);

  public abstract HookahDto map(Hookah model);

  public abstract Hookah map(HookahDto model);

}


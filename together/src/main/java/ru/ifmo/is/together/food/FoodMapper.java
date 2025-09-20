package ru.ifmo.is.together.food;

import org.mapstruct.*;
import ru.ifmo.is.together.cafe.CafeMapper;
import ru.ifmo.is.together.common.framework.CrudMapper;
import ru.ifmo.is.together.common.mapper.JsonNullableMapper;
import ru.ifmo.is.together.common.mapper.ReferenceMapper;

import ru.ifmo.is.together.food.dto.*;

import ru.ifmo.is.together.users.UserMapper;


@Mapper(
  uses = { JsonNullableMapper.class, ReferenceMapper.class, UserMapper.class, CafeMapper.class },
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class FoodMapper implements CrudMapper<Food, FoodDto, FoodCreateDto, Object> {
  public abstract Food map(FoodCreateDto dto);
  public abstract FoodDto map(Food model);
  public abstract Food map(FoodDto model);

}

package ru.ifmo.is.together.drink;

import org.mapstruct.*;
import ru.ifmo.is.together.cafe.CafeMapper;
import ru.ifmo.is.together.common.framework.CrudMapper;
import ru.ifmo.is.together.common.mapper.JsonNullableMapper;
import ru.ifmo.is.together.common.mapper.ReferenceMapper;
import ru.ifmo.is.together.drink.dto.*;
import ru.ifmo.is.together.food.Food;
import ru.ifmo.is.together.food.dto.*;
import ru.ifmo.is.together.users.UserMapper;

@Mapper(
  uses = { JsonNullableMapper.class, ReferenceMapper.class, UserMapper.class, CafeMapper.class },
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class DrinkMapper implements CrudMapper<Drink, DrinkDto, DrinkCreateDto, Object> {
  public abstract Drink map(DrinkCreateDto dto);

  public abstract DrinkDto map(Drink model);
  public abstract Drink map(FoodDto model);


}


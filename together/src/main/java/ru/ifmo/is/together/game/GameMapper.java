package ru.ifmo.is.together.game;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.ifmo.is.together.cafe.CafeMapper;
import ru.ifmo.is.together.common.framework.CrudMapper;
import ru.ifmo.is.together.common.mapper.JsonNullableMapper;
import ru.ifmo.is.together.common.mapper.ReferenceMapper;
import ru.ifmo.is.together.food.Food;
import ru.ifmo.is.together.food.dto.FoodCreateDto;
import ru.ifmo.is.together.food.dto.FoodDto;
import ru.ifmo.is.together.game.dto.GameCreateDto;
import ru.ifmo.is.together.game.dto.GameDto;
import ru.ifmo.is.together.users.UserMapper;

@Mapper(
  uses = { JsonNullableMapper.class, ReferenceMapper.class, UserMapper.class, CafeMapper.class },
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class GameMapper implements CrudMapper<Game, GameDto, GameCreateDto, Object> {
  public abstract Game map(GameCreateDto dto);
  public abstract GameDto map(Game model);
  public abstract Game map(GameDto model);

}


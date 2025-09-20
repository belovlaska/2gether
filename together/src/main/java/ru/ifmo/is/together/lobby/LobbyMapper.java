package ru.ifmo.is.together.lobby;


import org.mapstruct.*;
import ru.ifmo.is.together.common.framework.CrudMapper;
import ru.ifmo.is.together.common.mapper.JsonNullableMapper;
import ru.ifmo.is.together.common.mapper.ReferenceMapper;
import ru.ifmo.is.together.lobby.dto.LobbyCreateDto;
import ru.ifmo.is.together.lobby.dto.LobbyDto;
import ru.ifmo.is.together.users.UserMapper;

@Mapper(
  uses = { JsonNullableMapper.class, ReferenceMapper.class, UserMapper.class },
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class LobbyMapper implements CrudMapper<Lobby, LobbyDto, LobbyCreateDto, Object> {

  public abstract Lobby map(LobbyCreateDto dto);

  public abstract LobbyDto map(Lobby model);

  public abstract Lobby map(LobbyDto model);

}

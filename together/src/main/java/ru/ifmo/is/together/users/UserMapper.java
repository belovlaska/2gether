package ru.ifmo.is.together.users;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import ru.ifmo.is.together.common.framework.CrudMapper;
import ru.ifmo.is.together.common.mapper.JsonNullableMapper;
import ru.ifmo.is.together.common.mapper.ReferenceMapper;
import ru.ifmo.is.together.storage.StorageService;
import ru.ifmo.is.together.users.dto.*;

import java.util.stream.Collectors;

@Mapper(
  uses = { JsonNullableMapper.class, ReferenceMapper.class },
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedTargetPolicy = ReportingPolicy.IGNORE,
  imports = Collectors.class
)
public abstract class UserMapper implements CrudMapper<User, UserDto, UserUpdateDto, UserUpdateDto> {

  @Autowired
  public StorageService storageService;

  public abstract User map(UserUpdateDto dto);

  @Mapping(
    target = "photo",
    expression = "java(storageService.getFileUrl(model.getPhoto()))"
  )
  @Mapping(
    target = "roles",
    expression = "java(model.getRoles().stream().map(UserRole::getRole).collect(Collectors.toSet()))"
  )
  public abstract UserDto map(User model);

  public abstract User map(UserDto model);

  public abstract void update(UserUpdateDto dto, @MappingTarget User model);
}

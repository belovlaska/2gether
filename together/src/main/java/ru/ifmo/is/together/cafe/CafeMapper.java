package ru.ifmo.is.together.cafe;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Reference;
import ru.ifmo.is.together.cafe.dto.CafeCreateDto;
import ru.ifmo.is.together.cafe.dto.CafeDto;
import ru.ifmo.is.together.cafe.dto.CafeUpdateDto;
import ru.ifmo.is.together.common.framework.CrudMapper;
import ru.ifmo.is.together.common.mapper.JsonNullableMapper;
import ru.ifmo.is.together.common.mapper.ReferenceMapper;
import ru.ifmo.is.together.storage.StorageService;

@Mapper(
  uses = { JsonNullableMapper.class, ReferenceMapper.class },
  componentModel = MappingConstants.ComponentModel.SPRING,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class CafeMapper implements CrudMapper<Cafe, CafeDto, CafeCreateDto, CafeUpdateDto> {

  @Autowired
  public StorageService storageService;

  @Mapping(target = "owner", source = "ownerId")
  public abstract Cafe map(CafeCreateDto dto);

  @Mapping(target = "ownerId", source = "owner.id")
  @Mapping(target = "poster", expression = "java(storageService.getFileUrl(model.getPoster()))")
  public abstract CafeDto map(Cafe model);

  @Mapping(target = "owner", source = "ownerId")
  public abstract Cafe map(CafeDto model);

  public abstract void update(CafeUpdateDto dto, @MappingTarget Cafe model);
}

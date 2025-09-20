package ru.ifmo.is.together.reports;

import org.mapstruct.*;

import ru.ifmo.is.together.cafe.CafeMapper;
import ru.ifmo.is.together.common.framework.CrudMapper;
import ru.ifmo.is.together.common.mapper.JsonNullableMapper;
import ru.ifmo.is.together.common.mapper.ReferenceMapper;


import ru.ifmo.is.together.users.UserMapper;
import ru.ifmo.is.together.reports.dto.*;

@Mapper(
  uses = { JsonNullableMapper.class, ReferenceMapper.class, UserMapper.class, CafeMapper.class, UserMapper.class },
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ReportMapper implements CrudMapper<Report, ReportDto, ReportCreateDto, ReportUpdateDto> {
  public abstract Report map(ReportCreateDto dto);

  public abstract ReportDto map(Report model);

  public abstract Report map(ReportDto model);

  public abstract void update(ReportUpdateDto dto, @MappingTarget Report model);
}

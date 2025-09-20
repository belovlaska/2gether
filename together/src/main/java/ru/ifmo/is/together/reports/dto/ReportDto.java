package ru.ifmo.is.together.reports.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import ru.ifmo.is.together.cafe.dto.CafeDto;
import ru.ifmo.is.together.common.framework.dto.CrudDto;
import ru.ifmo.is.together.users.dto.UserDto;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportDto extends CrudDto {
  private int id;
  private UserDto sender;
  private CafeDto cafe;
  private UserDto user;
  private String issue;
  private String text;
  private Instant date;
  private boolean resolved;
  private Instant resolvedAt;
  private UserDto resolvedBy;
}

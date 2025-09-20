package ru.ifmo.is.together.game.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.ifmo.is.together.cafe.dto.CafeDto;
import ru.ifmo.is.together.common.framework.dto.CrudDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class GameDto extends CrudDto {

  private int id;
  private String name;
  private String genre;
  private Integer age_constraint;
  private String description;
  private CafeDto cafe;
}


package ru.ifmo.is.together.game.dto;

import lombok.Data;
import ru.ifmo.is.together.cafe.dto.CafeDto;

@Data
public class GameCreateDto {
  private String name;
  private String genre;
  private Integer age_constraint;
  private String description;
}


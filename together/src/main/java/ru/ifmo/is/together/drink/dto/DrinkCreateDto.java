package ru.ifmo.is.together.drink.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.ifmo.is.together.cafe.dto.CafeDto;
import ru.ifmo.is.together.common.framework.dto.CrudDto;
import ru.ifmo.is.together.users.dto.UserDto;

@Data

public class DrinkCreateDto {

  private String name;
  private String ingredients;
  private Integer cost;
  private Boolean isAlcoholic;
}

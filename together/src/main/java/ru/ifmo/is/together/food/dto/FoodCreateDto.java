package ru.ifmo.is.together.food.dto;

import lombok.Data;
import ru.ifmo.is.together.cafe.dto.CafeDto;
import ru.ifmo.is.together.users.dto.UserDto;

@Data
public class FoodCreateDto {

  private String name;
  private String ingredients;
  private Integer cost;
  private Boolean isHot;
  private Boolean isSpicy;
}

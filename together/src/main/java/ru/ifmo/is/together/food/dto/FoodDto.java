package ru.ifmo.is.together.food.dto;

import lombok.*;
import ru.ifmo.is.together.cafe.dto.CafeDto;
import ru.ifmo.is.together.common.framework.dto.CrudDto;
import ru.ifmo.is.together.ingredients.dto.IngredientDto;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class FoodDto extends CrudDto {

  private int id;
  private String name;
  private Set<IngredientDto> ingredients;
  private Integer cost;
  private Boolean isHot;
  private Boolean isSpicy;
  private CafeDto cafe;
}

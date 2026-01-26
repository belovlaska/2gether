package ru.ifmo.is.together.food.dto;

import lombok.Data;

import java.util.Set;

@Data
public class FoodCreateDto {

  private String name;
  private Set<Long> ingredientIds;
  private Integer cost;
  private Boolean isHot;
  private Boolean isSpicy;
}

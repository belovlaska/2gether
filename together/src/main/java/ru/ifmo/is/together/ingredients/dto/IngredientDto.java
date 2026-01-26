package ru.ifmo.is.together.ingredients.dto;

import lombok.Data;
import ru.ifmo.is.together.common.framework.dto.CrudDto;

@Data
public class IngredientDto extends CrudDto {

    private Long id;
    private String name;
}
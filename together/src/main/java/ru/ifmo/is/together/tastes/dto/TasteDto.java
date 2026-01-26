package ru.ifmo.is.together.tastes.dto;

import lombok.Data;
import ru.ifmo.is.together.common.framework.dto.CrudDto;

@Data
public class TasteDto extends CrudDto {

    private Long id;
    private String name;
}
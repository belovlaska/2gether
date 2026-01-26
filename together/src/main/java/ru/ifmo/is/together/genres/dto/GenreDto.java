package ru.ifmo.is.together.genres.dto;

import lombok.Data;
import ru.ifmo.is.together.common.framework.dto.CrudDto;

@Data
public class GenreDto extends CrudDto {

    private Long id;
    private String name;
}
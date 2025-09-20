package ru.ifmo.is.together.hookah.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.ifmo.is.together.cafe.dto.CafeDto;
import ru.ifmo.is.together.common.framework.dto.CrudDto;
import ru.ifmo.is.together.users.dto.UserDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class HookahDto extends CrudDto {

  private int id;
  private String tobacco;
  private Integer strength;
  private Integer cost;
  private String taste;
  private CafeDto cafe;
}

package ru.ifmo.is.together.hookah.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.ifmo.is.together.cafe.dto.CafeDto;
import ru.ifmo.is.together.common.framework.dto.CrudDto;
import ru.ifmo.is.together.users.dto.UserDto;

@Data
public class HookahCreateDto  {
  private String tobacco;
  private Integer strength;
  private Integer cost;
  private String taste;

}

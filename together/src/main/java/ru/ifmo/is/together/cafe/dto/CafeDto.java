package ru.ifmo.is.together.cafe.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.ifmo.is.together.common.framework.dto.CrudDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class CafeDto extends CrudDto {
  private int id;
  private String name;
  private String poster;
  private String description;
  private String address;
  private String workingHours;
  private Boolean alcoholPermission;
  private Boolean smokingPermission;
  private Integer ownerId;
}

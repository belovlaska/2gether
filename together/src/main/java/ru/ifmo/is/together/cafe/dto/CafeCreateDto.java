package ru.ifmo.is.together.cafe.dto;

import lombok.Data;

@Data
public class CafeCreateDto {

  private String name;
  private String description;
  private String address;
  private String workingHours;
  private Boolean alcoholPermission;
  private Boolean smokingPermission;
  private Integer ownerId;
}

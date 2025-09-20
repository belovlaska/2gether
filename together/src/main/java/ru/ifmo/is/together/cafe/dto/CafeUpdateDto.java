package ru.ifmo.is.together.cafe.dto;

import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;


@Data
public class CafeUpdateDto {

  private JsonNullable<String> name;

  private JsonNullable<String> description;

  private JsonNullable<String> address;

  private JsonNullable<String> workingHours;

  private JsonNullable<Boolean> alcoholPermission;

  private JsonNullable<Boolean> smokingPermission;
}

package ru.ifmo.is.together.reports.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
public class ReportUpdateDto {
  @NotNull
  @NotBlank
  @Length(min = 1, max = 127)
  private JsonNullable<String> issue;

  @NotNull
  private JsonNullable<String> text;

  @NotNull
  private JsonNullable<Boolean> resolved;
}

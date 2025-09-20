package ru.ifmo.is.together.reports.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

@Data
public class ReportCreateDto {
  @NotNull
  @NotBlank
  @Length(min = 1, max = 127)
  private String issue;

  private String text;
}

package ru.ifmo.is.together.refreshtokens.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Запрос на обновление Access Token")
public class RefreshDto {
  @Schema(description = "Refresh Token")
  @NotBlank(message = "Refresh Token не может быть пустыми")
  private String refreshToken;
}

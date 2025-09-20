package ru.ifmo.is.together.verificationtokens.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Запрос на подтверждение аккаунта")
public class VerificationDto {
  @Schema(description = "Verification Token")
  @NotBlank(message = "Verification Token не может быть пустыми")
  private String verificationToken;
}

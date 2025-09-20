package ru.ifmo.is.together.passwordreset.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "Запрос на смену пароля")
public class ResetPasswordDto {
  @Schema(description = "Новый пароль", example = "new_password")
  @Size(min = 6, max = 127, message = "Длина пароля должна быть от 6 до 127")
  @NotBlank(message = "Пароль не может быть пустыми")
  private String password;

  @Schema(description = "Токен из письма")
  @NotBlank(message = "Токен не может быть пустыми")
  private String passwordResetToken;
}

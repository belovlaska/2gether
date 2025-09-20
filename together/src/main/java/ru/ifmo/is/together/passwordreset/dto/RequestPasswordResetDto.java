package ru.ifmo.is.together.passwordreset.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "Запрос на отправку письма с токеном для смены пароля")
public class RequestPasswordResetDto {
  @Schema(description = "E-mail пользователя", example = "JohnyBoy@gmail.com")
  @Size(min = 3, max = 127, message = "E-mail пользователя должно содержать от 3 до 127 символов")
  @NotBlank(message = "E-mail пользователя не может быть пустыми")
  @Email(
    message = "Email is not valid",
    regexp = "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?"
  )
  private String email;
}

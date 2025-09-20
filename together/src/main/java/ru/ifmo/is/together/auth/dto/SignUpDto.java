package ru.ifmo.is.together.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на регистрацию")
public class SignUpDto {
  @Schema(description = "Имя пользователя", example = "JohnyBoy")
  @Size(min = 3, max = 63, message = "Имя пользователя должно содержать от 3 до 63 символов")
  @NotBlank(message = "Имя пользователя не может быть пустыми")
  private String username;

  @Schema(description = "E-mail пользователя", example = "JohnyBoy@gmail.com")
  @Size(min = 3, max = 127, message = "E-mail пользователя должно содержать от 3 до 127 символов")
  @NotBlank(message = "E-mail пользователя не может быть пустыми")
  @Email(
    message = "Email is not valid",
    regexp = "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?"
  )
  private String email;

  @Schema(description = "Пароль", example = "my_1secret1_password")
  @Size(min = 6, max = 127, message = "Длина пароля должна быть от 6 до 127")
  @NotBlank(message = "Пароль не может быть пустыми")
  private String password;
}

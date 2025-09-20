package ru.ifmo.is.together.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
public class UserUpdateDto {
  @NotNull
  private JsonNullable<String> username;

  @NotNull
  @Schema(description = "Текущий пароль", example = "current_password")
  @Size(min = 6, max = 127, message = "Длина пароля должна быть от 6 до 127")
  @NotBlank(message = "Пароль не может быть пустыми")
  private JsonNullable<String> currentPassword;

  @NotNull
  @Schema(description = "Новый пароль", example = "new_password")
  @Size(min = 6, max = 127, message = "Длина пароля должна быть от 6 до 127")
  @NotBlank(message = "Пароль не может быть пустыми")
  private JsonNullable<String> newPassword;

  @NotNull
  @Email(
    message = "Email is not valid",
    regexp = "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?"
  )
  private JsonNullable<String> email;

  @NotNull
  private JsonNullable<String> bio;
}

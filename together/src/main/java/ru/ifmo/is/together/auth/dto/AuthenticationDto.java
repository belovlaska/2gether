package ru.ifmo.is.together.auth.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import ru.ifmo.is.together.users.dto.UserDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ c токеном доступа")
public class AuthenticationDto {
  private final String tokenType = "Bearer";

  @Schema(description = "Токен доступа", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
  private String accessToken;

  @Schema(description = "Токен обновления", example = "e2a8a9c2-3a3f-...")
  private String refreshToken;

  private UserDto user;
}

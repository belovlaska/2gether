package ru.ifmo.is.together.userroles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.ifmo.is.together.userroles.Role;

@Data
@Schema(description = "Изменение роли пользователя")
public class UserRoleChangeDto {
  @Schema(description = "Роль", example = "ROLE_ADMIN")
  @NotNull
  private Role role;
}

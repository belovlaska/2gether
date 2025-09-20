package ru.ifmo.is.together.lobby.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.ifmo.is.together.cafe.dto.CafeDto;
import ru.ifmo.is.together.common.framework.dto.CrudDto;
import ru.ifmo.is.together.users.User;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class LobbyDto extends CrudDto {
  private int id;
  private CafeDto cafe;
  private String description;
  private LocalDateTime date;
  private int maxParticipants;
  private int currentParticipants;
  private Set<User> participants;
}

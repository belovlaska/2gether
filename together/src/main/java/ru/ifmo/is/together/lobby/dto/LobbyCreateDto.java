package ru.ifmo.is.together.lobby.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LobbyCreateDto {
  private LocalDateTime date;
  private String description;
  private int maxParticipants;
}

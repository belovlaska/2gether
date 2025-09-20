package ru.ifmo.is.together.lobby;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ifmo.is.together.cafe.CafeService;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.lobby.dto.LobbyCreateDto;
import ru.ifmo.is.together.lobby.dto.LobbyDto;
import ru.ifmo.is.together.users.UserService;
import ru.ifmo.is.together.users.dto.UserDto;

import java.util.Set;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Lobbiess")
public class LobbyController {
  private final LobbyService service;
  private final CafeService cafeService;
  private final UserService userService;

  @GetMapping("/lobbies/{id}/users")
  @Operation(summary = "Получить всех пользователей в лобби")
  public ResponseEntity<Set<UserDto>> users(@PathVariable int id, @PageableDefault(size = 20) Pageable pageable) {
    var lobby = service.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lobby Not Found: " + id));
    var users = service.getParticipants(lobby, pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(users.size()))
      .body(users);
  }

  @PostMapping("/lobbies/{id}/users")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Добавиться в лобби", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<LobbyDto> addUser(@PathVariable int id) throws Exception {
    var lobby = service.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lobby Not Found: " + id));
    return ResponseEntity.status(HttpStatus.CREATED).body(service.addUser(id));
  }

  @DeleteMapping("/lobbies/{id}/users")
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Выйти из лобби", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<Void> deleteUser(@PathVariable int id) {
    var lobby = service.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lobby Not Found: " + id));
    service.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/cafes/{cafeId}/lobbies")
  @Operation(summary = "Получить все лобби в заданном антикафе")
  public ResponseEntity<Page<LobbyDto>> cafeLobbies(@PathVariable int cafeId, @PageableDefault(size = 20) Pageable pageable) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var lobbies = service.getCafeLobbies(cafe, pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(lobbies.getTotalElements()))
      .body(lobbies);
  }

  @GetMapping("/users/{userId}/lobbies")
  @Operation(summary = "Получить все лобби пользователя")
  public ResponseEntity<Page<LobbyDto>> userLobbies(@PathVariable int userId, @PageableDefault(size = 20) Pageable pageable) {
    var user = userService.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    var lobbies = service.getUserLobbies(user, pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(lobbies.getTotalElements()))
      .body(lobbies);
  }

  @GetMapping("/lobbies/{id}")
  @Operation(summary = "Получить лобби по ID")
  public ResponseEntity<LobbyDto> show(@PathVariable int id) {
    var lobby = service.getById(id);
    return ResponseEntity.ok(lobby);
  }

  @PostMapping("/cafes/{cafeId}/lobbies")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Создать лобби", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<LobbyDto> create(@Valid @RequestBody LobbyCreateDto dto, @PathVariable int cafeId) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var lobby = service.create(dto, cafe);
    return ResponseEntity.status(HttpStatus.CREATED).body(lobby);
  }

//  @DeleteMapping("/lobbies/{id}")
//  @PreAuthorize("hasRole('USER')")
//  @Operation(summary = "Удалить лобби по ID", security = @SecurityRequirement(name = "bearerAuth"))
//  public ResponseEntity<Void> delete(@PathVariable int id) {
//    if (service.delete(id)) {
//      return ResponseEntity.noContent().build();
//    }
//    return ResponseEntity.notFound().build();
//  }
}

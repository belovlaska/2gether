package ru.ifmo.is.together.game;

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
import ru.ifmo.is.together.common.search.SearchDto;
import ru.ifmo.is.together.food.FoodService;
import ru.ifmo.is.together.food.dto.FoodCreateDto;
import ru.ifmo.is.together.food.dto.FoodDto;
import ru.ifmo.is.together.game.dto.GameCreateDto;
import ru.ifmo.is.together.game.dto.GameDto;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Game")
public class GameController {

  private final GameService service;
  private final CafeService cafeService;


  //надо не для всей еды, а для еды в антикафе
  @PostMapping("/cafes/{cafeId}/games/search") //тут сделать в пути cafes
  @Operation(summary = "Поиск и фильтрация игр")
  public ResponseEntity<Page<GameDto>> search(
    @PathVariable int cafeId,
    @PageableDefault(size = 20) Pageable pageable,
    @RequestBody(required = false) SearchDto request
  ) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var game = service.findBySearchCriteriaAndCafe( request, cafe, pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(game.getTotalElements()))
      .body(game);
  }


  @GetMapping("/cafes/{cafeId}/games")
  @Operation(summary = "Получить все игры кафе")
  public ResponseEntity<Page<GameDto>> cafeGame(@PathVariable int cafeId, @PageableDefault(size = 20) Pageable pageable) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var game = service.getCafeFood(cafe, pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(game.getTotalElements()))
      .body(game);
  }


  @PostMapping("/cafes/{cafeId}/games")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Создать игру для кафе", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<GameDto> create(@PathVariable int cafeId, @Valid @RequestBody GameCreateDto dto) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var game = service.create(dto, cafe);
    return ResponseEntity.status(HttpStatus.CREATED).body(game);
  }


  @DeleteMapping("/games/{id}")
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Удалить игру по ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<Void> delete(@PathVariable int id) {
    if (service.delete(id)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}


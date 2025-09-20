package ru.ifmo.is.together.drink;

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
import ru.ifmo.is.together.drink.dto.*;
import ru.ifmo.is.together.food.FoodService;
import ru.ifmo.is.together.food.dto.*;
import ru.ifmo.is.together.users.UserService;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Drink")
public class DrinkController {

  private final DrinkService service;
  private final CafeService cafeService;

  @PostMapping("/cafes/{cafeId}/drinks/search")
  @Operation(summary = "Поиск и фильтрация напитков")
  public ResponseEntity<Page<DrinkDto>> search(
    @PathVariable int cafeId,
    @PageableDefault(size = 20) Pageable pageable,
    @RequestBody(required = false) SearchDto request
  ) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var drink = service.findBySearchCriteria(request, cafe, pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(drink.getTotalElements()))
      .body(drink);
  }


  @GetMapping("/cafes/{cafeId}/drinks")
  @Operation(summary = "Получить все напитки кафе")
  public ResponseEntity<Page<DrinkDto>> cafeDrinks(@PathVariable int cafeId, @PageableDefault(size = 20) Pageable pageable) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var drink = service.getCafeDrink(cafe, pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(drink.getTotalElements()))
      .body(drink);
  }


  @PostMapping("/cafes/{cafeId}/drinks")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Создать напитки для кафе", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<DrinkDto> create(@PathVariable int cafeId, @Valid @RequestBody DrinkCreateDto dto) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var drink = service.create(dto, cafe);
    return ResponseEntity.status(HttpStatus.CREATED).body(drink);
  }


  @DeleteMapping("/drinks/{id}")
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Удалить напитки по ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<Void> delete(@PathVariable int id) {
    if (service.delete(id)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}


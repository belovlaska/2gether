package ru.ifmo.is.together.food;


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
import ru.ifmo.is.together.food.dto.*;
import ru.ifmo.is.together.users.UserService;


@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Food")
public class FoodController {

  private final FoodService service;
  private final CafeService cafeService;


  //надо не для всей еды, а для еды в антикафе
  @PostMapping("/cafes/{cafeId}/foods/search") //тут сделать в пути cafes
  @Operation(summary = "Поиск и фильтрация еды")
  public ResponseEntity<Page<FoodDto>> search(
    @PathVariable int cafeId,
    @PageableDefault(size = 20) Pageable pageable,
    @RequestBody(required = false) SearchDto request
  ) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var food = service.findBySearchCriteriaAndCafe( request, cafe, pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(food.getTotalElements()))
      .body(food);
  }


  @GetMapping("/cafes/{cafeId}/foods")
  @Operation(summary = "Получить всю еду кафе")
  public ResponseEntity<Page<FoodDto>> cafeFood(@PathVariable int cafeId, @PageableDefault(size = 20) Pageable pageable) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var food = service.getCafeFood(cafe, pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(food.getTotalElements()))
      .body(food);
  }


  @PostMapping("/cafes/{cafeId}/foods")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Создать еду для кафе", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<FoodDto> create(@PathVariable int cafeId, @Valid @RequestBody FoodCreateDto dto) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var food = service.create(dto, cafe);
    return ResponseEntity.status(HttpStatus.CREATED).body(food);
  }


  @DeleteMapping("/foods/{id}")
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Удалить еду по ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<Void> delete(@PathVariable int id) {
    if (service.delete(id)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}

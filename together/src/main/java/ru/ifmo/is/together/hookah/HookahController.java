package ru.ifmo.is.together.hookah;

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

import ru.ifmo.is.together.hookah.dto.*;
import ru.ifmo.is.together.users.UserService;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Hookah")
public class HookahController {

  private final HookahService service;
  private final CafeService cafeService;


  @PostMapping("/cafes/{cafeId}/hookahs/search")
  @Operation(summary = "Поиск и фильтрация кальянов")
  public ResponseEntity<Page<HookahDto>> search(
    @PathVariable int cafeId,
    @PageableDefault(size = 20) Pageable pageable,
    @RequestBody(required = false) SearchDto request
  ) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var hookahs = service.findBySearchCriteriaAndCafe(request, cafe, pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(hookahs.getTotalElements()))
      .body(hookahs);
  }


  @GetMapping("/cafes/{cafeId}/hookahs")
  @Operation(summary = "Получить все кальяны кафе")
  public ResponseEntity<Page<HookahDto>> cafeHookah(@PathVariable int cafeId, @PageableDefault(size = 20) Pageable pageable) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var hookah = service.getCafeHookah(cafe, pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(hookah.getTotalElements()))
      .body(hookah);
  }


  @PostMapping("/cafes/{cafeId}/hookahs")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Создать кальян для кафе", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<HookahDto> create(@PathVariable int cafeId, @Valid @RequestBody HookahCreateDto dto) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var food = service.create(dto, cafe);
    return ResponseEntity.status(HttpStatus.CREATED).body(food);
  }


  @DeleteMapping("/hookahs/{id}")
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Удалить кальян по ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<Void> delete(@PathVariable int id) {
    if (service.delete(id)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}


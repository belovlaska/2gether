package ru.ifmo.is.together.reports;

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
import ru.ifmo.is.together.reports.dto.ReportCreateDto;
import ru.ifmo.is.together.reports.dto.ReportDto;
import ru.ifmo.is.together.reports.dto.ReportUpdateDto;
import ru.ifmo.is.together.users.UserService;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Reports")
public class ReportController {

  private final ReportService service;
  private final CafeService cafeService;
  private final UserService userService;

  @GetMapping("/reports")
  @Operation(summary = "Получить все жалобы")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<ReportDto>> index(@PageableDefault(size = 20) Pageable pageable) {
    var reports = service.getAll(pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(reports.getTotalElements()))
      .body(reports);
  }

  @GetMapping("/reports/pending")
  @Operation(summary = "Получить все нерассмотренные жалобы")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<ReportDto>> pending(@PageableDefault(size = 20) Pageable pageable) {
    var reports = service.getPending(pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(reports.getTotalElements()))
      .body(reports);
  }

  @PostMapping("/reports/search")
  @Operation(summary = "Поиск и фильтрация жалоб")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<ReportDto>> search(
    @PageableDefault(size = 20) Pageable pageable,
    @RequestBody(required = false) SearchDto request
  ) {
    var reports = service.findBySearchCriteria(request, pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(reports.getTotalElements()))
      .body(reports);
  }

  @GetMapping("/reports/{id}")
  @Operation(summary = "Получить жалобу по ID")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ReportDto> show(@PathVariable int id) {
    var report = service.getById(id);
    return ResponseEntity.ok(report);
  }

  @PostMapping("/cafes/{cafeId}/reports")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Пожаловаться на антикафе", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ReportDto> reportCafe(@PathVariable int cafeId, @Valid @RequestBody ReportCreateDto dto) {
    var cafe = cafeService.findById(cafeId).orElseThrow(() -> new ResourceNotFoundException("Cafe not found: " + cafeId));
    var report = service.create(dto, cafe);
    return ResponseEntity.status(HttpStatus.CREATED).body(report);
  }

  @PostMapping("/users/{userId}/reports")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Пожаловаться на пользователя", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ReportDto> reportUser(@PathVariable int userId, @Valid @RequestBody ReportCreateDto dto) {
    var user = userService.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    var report = service.create(dto, user);
    return ResponseEntity.status(HttpStatus.CREATED).body(report);
  }

  @PatchMapping("/reports/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Обновить жалобу по ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ReportDto> update(@PathVariable int id, @Valid @RequestBody ReportUpdateDto dto) {
    var report = service.update(dto, id);
    return ResponseEntity.ok(report);
  }

  @DeleteMapping("/reports/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Удалить жалобу по ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<Void> delete(@PathVariable int id) {
    if (service.delete(id)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}

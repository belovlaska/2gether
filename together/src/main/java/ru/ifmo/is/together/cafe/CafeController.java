package ru.ifmo.is.together.cafe;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ru.ifmo.is.together.cafe.dto.CafeCreateDto;
import ru.ifmo.is.together.cafe.dto.CafeDto;
import ru.ifmo.is.together.cafe.dto.CafeUpdateDto;
import ru.ifmo.is.together.common.errors.FileIsEmptyError;
import ru.ifmo.is.together.common.search.SearchDto;
import ru.ifmo.is.together.cafe.dto.*;

import javax.naming.LimitExceededException;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping(value = "/api/cafes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Cafes")
public class CafeController {

  private final CafeService service;

  @GetMapping
  @Operation(summary = "Получить все антикафе")
  public ResponseEntity<Page<CafeDto>> index(@PageableDefault(size = 20) Pageable pageable) {
    var cafes = service.getAll(pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(cafes.getTotalElements()))
      .body(cafes);
  }

  @PostMapping("/search")
  @Operation(summary = "Поиск и фильтрация антикафе")
  public ResponseEntity<Page<CafeDto>> search(
    @PageableDefault(size = 20) Pageable pageable,
    @RequestBody(required = false) SearchDto request
  ) {
    var cafes = service.findBySearchCriteria(request, pageable);
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(cafes.getTotalElements()))
      .body(cafes);
  }

  @PostMapping(path = "/{id}/poster", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Загрузить постер антикафе", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<CafeDto> upload(@PathVariable int id, @RequestParam("file") MultipartFile file) throws Exception {
    if (file.isEmpty()) {
      throw new FileIsEmptyError("File not found");
    }

    if (file.getSize() / (1024 * 1024) > 10) {
      throw new LimitExceededException("Image size must be less than 10MB");
    }

    var byteArray = new ByteArrayOutputStream();
    IOUtils.copy(file.getInputStream(), byteArray);
    var bytes = byteArray.toByteArray();

    var cafe = service.upload(id, file.getOriginalFilename(), bytes, file.getContentType());
    return ResponseEntity.ok(cafe);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить антикафе по ID")
  public ResponseEntity<CafeDto> show(@PathVariable int id) {
    var cafe = service.getById(id);
    return ResponseEntity.ok(cafe);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Создать антикафе", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<CafeDto> create(@Valid @RequestBody CafeCreateDto dto) {
    var cafe = service.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(cafe);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Обновить антикафе по ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<CafeDto> update(@PathVariable int id, @Valid @RequestBody CafeUpdateDto dto) {
    var cafe = service.update(dto, id);
    return ResponseEntity.ok(cafe);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Удалить антикафе по ID", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<Void> delete(@PathVariable int id) {
    if (service.delete(id)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}

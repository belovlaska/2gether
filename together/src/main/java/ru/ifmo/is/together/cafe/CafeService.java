package ru.ifmo.is.together.cafe;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.cafe.CafeMapper;
import ru.ifmo.is.together.cafe.CafePolicy;
import ru.ifmo.is.together.cafe.CafeRepository;
import ru.ifmo.is.together.cafe.dto.CafeCreateDto;
import ru.ifmo.is.together.cafe.dto.CafeDto;
import ru.ifmo.is.together.cafe.dto.CafeUpdateDto;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.application.ApplicationService;
import ru.ifmo.is.together.common.search.SearchDto;
import ru.ifmo.is.together.common.search.SearchMapper;
import ru.ifmo.is.together.common.utils.images.ImageProcessor;
import ru.ifmo.is.together.storage.StorageService;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CafeService extends ApplicationService {

  private static final Logger logger = LoggerFactory.getLogger(CafeService.class);

  private final CafeMapper mapper;
  private final CafePolicy policy;
  private final CafeRepository repository;

  private final SearchMapper<Cafe> searchMapper;

  private final StorageService storageService;
  private final ImageProcessor imageProcessor;

  public Optional<Cafe> findById(int id) {
    return repository.findById(id);
  }

  public Page<CafeDto> getAll(Pageable pageable) {
    policy.showAll(currentUser());

    return repository.findAll(pageable).map(mapper::map);
  }

  public Page<CafeDto> findBySearchCriteria(SearchDto searchData, Pageable pageable) {
    policy.search(currentUser());

    return repository.findAll(searchMapper.map(searchData), pageable).map(mapper::map);
  }

  @Transactional
  public CafeDto create(CafeCreateDto dto) {
    policy.create(currentUser());

    var cafe = mapper.map(dto);
    repository.save(cafe);
    return mapper.map(cafe);
  }

  public CafeDto getById(int id) {
    var cafe = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
    policy.show(currentUser(), cafe);

    return mapper.map(cafe);
  }

  @Transactional
  public CafeDto update(CafeUpdateDto objData, int id) {
    var cafe = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
    policy.update(currentUser(), cafe);

    mapper.update(objData, cafe);
    repository.save(cafe);

    return mapper.map(cafe);
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public boolean delete(int id) {
    var cafe = repository.findById(id);
    return cafe.map(o -> {
      policy.delete(currentUser(), o);
      if (o.getPoster() != null) {
        try {
          storageService.delete(o.getPoster());
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
      repository.delete(o);
      return true;
    }).orElse(false);
  }

  @Transactional
  public CafeDto upload(int id, String filename, byte[] bytes, String contentType) throws Exception {
    var cafe = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));

    if (!imageProcessor.checkImage(bytes) || !imageProcessor.checkContentType(contentType)) {
      throw new IOException("Bad image");
    }

    var image = imageProcessor.createMagickImageFromBytes(filename, bytes);
    var bais = imageProcessor.save(image, contentType);

    // Upload poster
    var newImageName = "cafes-poster-" + UUID.randomUUID() + imageProcessor.getImageExtension(contentType);
    try {
      storageService.create(newImageName, contentType, bais, (long) bais.available());
    } catch (Exception e) {
      throw new RuntimeException("Failed to upload new image: " + e.getMessage(), e);
    }

    // Delete old poster if exists
    if (cafe.getPoster() != null) {
      try {
        var oldFileName = cafe.getPoster();
        storageService.delete(oldFileName);
      } catch (Exception e) {
        logger.error("Failed to delete old poster for cafe {}: {}", cafe.getId(), e.getMessage());
      }
    }

    cafe.setPoster(newImageName);
    return mapper.map(repository.save(cafe));
  }
}

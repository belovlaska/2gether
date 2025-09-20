package ru.ifmo.is.together.users;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.openapitools.jackson.nullable.JsonNullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import ru.ifmo.is.together.auth.events.OnPasswordResetRequestEvent;
import ru.ifmo.is.together.auth.events.OnRegistrationCompleteEvent;
import ru.ifmo.is.together.common.caching.RequestCache;
import ru.ifmo.is.together.common.config.PasswordEncoderProvider;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.errors.UserWithThisUsernameAlreadyExists;
import ru.ifmo.is.together.common.search.SearchDto;
import ru.ifmo.is.together.common.search.SearchMapper;
import ru.ifmo.is.together.common.utils.images.ImageProcessor;
import ru.ifmo.is.together.storage.StorageService;
import ru.ifmo.is.together.userroles.Role;
import ru.ifmo.is.together.userroles.UserRole;
import ru.ifmo.is.together.userroles.UserRoleService;
import ru.ifmo.is.together.users.dto.UserDto;
import ru.ifmo.is.together.users.dto.UserUpdateDto;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final UserMapper mapper;
  private final UserPolicy policy;
  private final SearchMapper<User> searchMapper;
  private final UserRepository repository;
  private final PasswordEncoderProvider passwordEncoderProvider;
  private final UserRoleService userRoleService;

  private final StorageService storageService;
  private final ImageProcessor imageProcessor;

  private final HttpServletRequest httpRequest;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * Сохранение пользователя
   *
   * @return сохраненный пользователь
   */
  @Transactional
  public User save(User user) {
    return repository.save(user);
  }

  public Optional<User> findByEmail(String email) {
    return repository.findByEmail(email);
  }

  public Optional<User> findById(int id) {
    return repository.findById(id);
  }

  /**
   * Создание пользователя
   *
   * @return созданный пользователь
   */
  @Transactional
  public User create(User user) {
    if (repository.existsByUsername(user.getUsername())) {
      throw new UserWithThisUsernameAlreadyExists("Пользователь с таким именем уже существует");
    }

    if (repository.existsByEmail(user.getEmail())) {
      throw new UserWithThisUsernameAlreadyExists("Этот email уже зарегистрирован");
    }

    user = save(user);
    if (repository.count() == 1) {
      var roles = new HashSet<UserRole>();
      logger.info("Creating first user with ADMIN roles");
      roles.add(UserRole.builder().role(Role.ROLE_USER).user(user).build());
      roles.add(UserRole.builder().role(Role.ROLE_ADMIN).user(user).build());
      return save(userRoleService.addRoles(user, roles));
    }

    sendConfirmation(user, true);
    return user;
  }

  /**
   * Подтверждение аккаунта пользователя
   *
   * @return подтвержденный пользователь
   */
  @Transactional
  public User confirm(User user) {
    var roles = new HashSet<UserRole>();
    roles.add(UserRole.builder().role(Role.ROLE_USER).user(user).build());
    return save(userRoleService.addRoles(user, roles));
  }

  public void sendConfirmation(User user, boolean newUser) {
    eventPublisher.publishEvent(
      new OnRegistrationCompleteEvent(user, newUser, httpRequest.getLocale())
    );
  }

  public void sendPasswordReset(User user) {
    eventPublisher.publishEvent(
      new OnPasswordResetRequestEvent(user, httpRequest.getLocale())
    );
  }

  /**
   * Получение пользователя по имени пользователя
   *
   * @return пользователь
   */
  @RequestCache
  public User getByUsername(String username) {
    return repository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
  }

  /**
   * Получение пользователя по имени пользователя
   * <p>
   * Нужен для Spring Security
   *
   * @return пользователь
   */
  public UserDetailsService userDetailsService() {
    return this::getByUsername;
  }

  /**
   * Получение текущего пользователя
   *
   * @return текущий пользователь
   */
  @RequestCache
  public User getCurrentUser() {
    // Получение имени пользователя из контекста Spring Security
    var username = getCurrentUsername();
    return getByUsername(username);
  }

  /**
   * Получение имени текущего пользователя
   *
   * @return имя текущего пользователь
   */
  @RequestCache
  public String getCurrentUsername() {
    // Получение имени пользователя из контекста Spring Security
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  public Page<UserDto> getAll(Pageable pageable) {
    policy.showAll(currentUser());

    var objs = repository.findAll(pageable);
    return objs.map(mapper::map);
  }

  public Page<UserDto> findBySearchCriteria(SearchDto searchData, Pageable pageable) {
    policy.search(currentUser());

    var objs = repository.findAll(searchMapper.map(searchData), pageable);
    return objs.map(mapper::map);
  }

  public UserDto getById(int id) {
    var obj = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
    policy.show(currentUser(), obj);

    return mapper.map(obj);
  }

  @Transactional
  public UserDto update(UserUpdateDto objData, int id) {
    var obj = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
    policy.update(currentUser(), obj);

    if (objData.getNewPassword() != null && objData.getNewPassword().isPresent()
      && objData.getEmail() != null && objData.getEmail().isPresent()) {
      throw new RuntimeException("You cannot update both email and password");
    }

    if (objData.getEmail() != null && objData.getEmail().isPresent()) {
      if (objData.getEmail().get().equals(obj.getEmail())) {
        objData.setEmail(JsonNullable.undefined());
      } else if (Objects.requireNonNull(currentUser()).getId() == obj.getId()) {
        logger.info("User {} changing his own email: sending confirmation mail", obj.getId());
        obj = save(userRoleService.deleteRole(obj, Role.ROLE_USER));
        sendConfirmation(obj, false);
      }
    }

    if (objData.getNewPassword() != null && objData.getNewPassword().isPresent()) {
      if (objData.getCurrentPassword() == null || !objData.getCurrentPassword().isPresent()) {
        throw new AuthenticationCredentialsNotFoundException("Please, enter your current password to set new one");
      }

      var passwordEncoder = passwordEncoderProvider.encoder();
      if (!passwordEncoder.matches(objData.getCurrentPassword().get(), obj.getPassword())) {
        throw new BadCredentialsException("Current password does not match");
      }

      if (!passwordEncoder.matches(objData.getNewPassword().get(), obj.getPassword())) {
        var newPasswordHashed = passwordEncoder.encode(objData.getNewPassword().get());
        obj.setPassword(newPasswordHashed);
      }
    }

    mapper.update(objData, obj);
    repository.save(obj);

    return mapper.map(obj);
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public boolean delete(int id) {
    var obj = repository.findById(id);
    return obj.map(o -> {
      policy.delete(currentUser(), o);
      if (o.getPhoto() != null) {
        try {
          storageService.delete(o.getPhoto());
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
      repository.delete(o);
      return true;
    }).orElse(false);
  }

  @Transactional
  public UserDto upload(String filename, byte[] bytes, long fileSize, String contentType)
    throws Exception {

    if (!imageProcessor.checkImage(bytes) || !imageProcessor.checkContentType(contentType)) {
      throw new IOException("Bad image");
    }

    // Crop image
    var image = imageProcessor.createMagickImageFromBytes(filename, bytes);
    var bais = imageProcessor.cropToSquare(image, contentType);

    // Upload new image
    var newImageName = "users-photo-" + UUID.randomUUID() + imageProcessor.getImageExtension(contentType);
    try {
      storageService.create(newImageName, contentType, bais, (long) bais.available());
    } catch (Exception e) {
      throw new RuntimeException("Failed to upload new image: " + e.getMessage(), e);
    }

    // Delete old image if exists
    var currentUser = getCurrentUser();
    if (currentUser.getPhoto() != null) {
      try {
        var oldFileName = currentUser.getPhoto();
        storageService.delete(oldFileName);
      } catch (Exception e) {
        logger.error("Failed to delete old image for user {}: {}", currentUser.getId(), e.getMessage());
      }
    }

    currentUser.setPhoto(newImageName);
    return mapper.map(save(currentUser));
  }

  @RequestCache
  private User currentUser() {
    try {
      return getCurrentUser();
    } catch (UsernameNotFoundException _ex) {
      return null;
    }
  }
}

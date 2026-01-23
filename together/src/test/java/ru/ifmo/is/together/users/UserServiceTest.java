package ru.ifmo.is.together.users;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import ru.ifmo.is.together.auth.events.OnPasswordResetRequestEvent;
import ru.ifmo.is.together.auth.events.OnRegistrationCompleteEvent;
import ru.ifmo.is.together.common.config.PasswordEncoderProvider;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.errors.UserWithThisUsernameAlreadyExists;
import ru.ifmo.is.together.common.search.SearchDto;
import ru.ifmo.is.together.common.search.SearchMapper;
import ru.ifmo.is.together.common.utils.images.ImageProcessor;
import ru.ifmo.is.together.storage.StorageService;
import ru.ifmo.is.together.userroles.UserRoleService;
import ru.ifmo.is.together.users.dto.UserDto;
import ru.ifmo.is.together.users.dto.UserUpdateDto;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper mapper;

    @Mock
    private UserPolicy policy;

    @Mock
    private SearchMapper<User> searchMapper;

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoderProvider passwordEncoderProvider;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private StorageService storageService;

    @Mock
    private ImageProcessor imageProcessor;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private MockHttpServletRequest mockHttpRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1)
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .build();
        
        mockHttpRequest = new MockHttpServletRequest();
    }

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUserSuccessfully() {
        // Given
        when(repository.save(any(User.class))).thenReturn(testUser);

        // When
        User savedUser = userService.save(testUser);

        // Then
        assertEquals(testUser, savedUser);
        verify(repository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Given
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> foundUser = userService.findByEmail("test@example.com");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(testUser, foundUser.get());
    }

    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        // Given
        when(repository.findById(1)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> foundUser = userService.findById(1);

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(testUser, foundUser.get());
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Given
        when(repository.existsByUsername("testuser")).thenReturn(false);
        when(repository.existsByEmail("test@example.com")).thenReturn(false);
        when(repository.count()).thenReturn(1L);
        when(repository.save(any(User.class))).thenReturn(testUser);
        when(httpRequest.getLocale()).thenReturn(java.util.Locale.getDefault());

        // When
        User createdUser = userService.create(testUser);

        // Then
        assertEquals(testUser, createdUser);
        verify(repository, times(2)).save(any(User.class));
        verify(eventPublisher, never()).publishEvent(any(OnRegistrationCompleteEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Given
        when(repository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        assertThrows(UserWithThisUsernameAlreadyExists.class, () -> {
            userService.create(testUser);
        });
        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(repository.existsByUsername("testuser")).thenReturn(false);
        when(repository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        assertThrows(UserWithThisUsernameAlreadyExists.class, () -> {
            userService.create(testUser);
        });
        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should confirm user successfully")
    void shouldConfirmUserSuccessfully() {
        // Given
        when(repository.save(any(User.class))).thenReturn(testUser);

        // When
        User confirmedUser = userService.confirm(testUser);

        // Then
        assertEquals(testUser, confirmedUser);
        verify(userRoleService, times(1)).addRoles(eq(testUser), any());
        verify(repository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should get user by username successfully")
    void shouldGetUserByUsernameSuccessfully() {
        // Given
        when(repository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        User foundUser = userService.getByUsername("testuser");

        // Then
        assertEquals(testUser, foundUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found by username")
    void shouldThrowExceptionWhenUserNotFoundByUsername() {
        // Given
        when(repository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getByUsername("nonexistent");
        });
    }

    @Test
    @DisplayName("Should get current user successfully")
    void shouldGetCurrentUserSuccessfully() {
        // Given
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new org.springframework.security.authentication.TestingAuthenticationToken(
                "testuser", "password", new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        User currentUser = userService.getCurrentUser();

        // Then
        assertEquals(testUser, currentUser);
    }

    @Test
    @DisplayName("Should get all users successfully")
    void shouldGetAllUsersSuccessfully() {
        // Given
        Pageable pageable = Pageable.unpaged();
        Page<User> userPage = new PageImpl<>(java.util.List.of(testUser));
        UserDto userDto = new UserDto();
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new org.springframework.security.authentication.TestingAuthenticationToken(
                "admin", "password", new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("admin")).thenReturn(Optional.of(
            User.builder().id(2).username("admin").build()));
        when(repository.findAll(pageable)).thenReturn(userPage);
        when(mapper.map(testUser)).thenReturn(userDto);

        // When
        Page<UserDto> result = userService.getAll(pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(userDto, result.getContent().get(0));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void shouldGetUserByIdSuccessfully() {
        // Given
        UserDto userDto = new UserDto();
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new org.springframework.security.authentication.TestingAuthenticationToken(
                "testuser", "password", new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("testuser")).thenReturn(Optional.of(
            User.builder().id(1).username("testuser").build()));
        when(repository.findById(1)).thenReturn(Optional.of(testUser));
        when(mapper.map(testUser)).thenReturn(userDto);

        // When
        UserDto result = userService.getById(1);

        // Then
        assertEquals(userDto, result);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void shouldThrowExceptionWhenUserNotFoundById() {
        // Given
        when(repository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getById(999);
        });
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        when(repository.findById(1)).thenReturn(Optional.of(testUser));
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new org.springframework.security.authentication.TestingAuthenticationToken(
                "admin", "password", new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("admin")).thenReturn(Optional.of(
            User.builder().id(2).username("admin").build()));

        // When
        boolean result = userService.delete(1);

        // Then
        assertTrue(result);
        verify(repository, times(1)).delete(testUser);
    }

    @Test
    @DisplayName("Should return false when user to delete not found")
    void shouldReturnFalseWhenUserToDeleteNotFound() {
        // Given
        when(repository.findById(999)).thenReturn(Optional.empty());

        // When
        boolean result = userService.delete(999);

        // Then
        assertFalse(result);
    }
}
package ru.ifmo.is.together.cafe;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.TestingAuthenticationToken;

import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.search.SearchDto;
import ru.ifmo.is.together.common.search.SearchMapper;
import ru.ifmo.is.together.common.utils.images.ImageProcessor;
import ru.ifmo.is.together.storage.StorageService;
import ru.ifmo.is.together.users.User;
import ru.ifmo.is.together.cafe.dto.CafeCreateDto;
import ru.ifmo.is.together.cafe.dto.CafeDto;
import ru.ifmo.is.together.cafe.dto.CafeUpdateDto;

@ExtendWith(MockitoExtension.class)
class CafeServiceTest {

    @Mock
    private CafeMapper mapper;

    @Mock
    private CafePolicy policy;

    @Mock
    private CafeRepository repository;

    @Mock
    private SearchMapper<Cafe> searchMapper;

    @Mock
    private StorageService storageService;

    @Mock
    private ImageProcessor imageProcessor;

    @InjectMocks
    private CafeService cafeService;

    private Cafe testCafe;
    private User testUser;

    @BeforeEach
    void setUp() {
        testCafe = Cafe.builder()
                .id(1)
                .name("Test Cafe")
                .address("Test Address")
                .build();

        testUser = User.builder()
                .id(1)
                .username("testuser")
                .build();
    }

    @Test
    @DisplayName("Should find cafe by ID successfully")
    void shouldFindCafeByIdSuccessfully() {
        // Given
        when(repository.findById(1)).thenReturn(Optional.of(testCafe));

        // When
        Optional<Cafe> result = cafeService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCafe, result.get());
    }

    @Test
    @DisplayName("Should return empty optional when cafe not found by ID")
    void shouldReturnEmptyOptionalWhenCafeNotFoundById() {
        // Given
        when(repository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Cafe> result = cafeService.findById(999);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should get all cafes successfully")
    void shouldGetAllCafesSuccessfully() {
        // Given
        Pageable pageable = Pageable.unpaged();
        Page<Cafe> cafePage = new PageImpl<>(java.util.List.of(testCafe));
        CafeDto cafeDto = new CafeDto();
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("admin", "password", new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("admin")).thenReturn(Optional.of(testUser));
        when(repository.findAll(pageable)).thenReturn(cafePage);
        when(mapper.map(testCafe)).thenReturn(cafeDto);

        // When
        Page<CafeDto> result = cafeService.getAll(pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(cafeDto, result.getContent().get(0));
        verify(policy, times(1)).showAll(testUser);
    }

    @Test
    @DisplayName("Should find cafes by search criteria successfully")
    void shouldFindCafesBySearchCriteriaSuccessfully() {
        // Given
        Pageable pageable = Pageable.unpaged();
        Page<Cafe> cafePage = new PageImpl<>(java.util.List.of(testCafe));
        CafeDto cafeDto = new CafeDto();
        SearchDto searchDto = new SearchDto();
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("admin", "password", new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("admin")).thenReturn(Optional.of(testUser));
        when(repository.findAll(any(), eq(pageable))).thenReturn(cafePage);
        when(mapper.map(testCafe)).thenReturn(cafeDto);

        // When
        Page<CafeDto> result = cafeService.findBySearchCriteria(searchDto, pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(cafeDto, result.getContent().get(0));
        verify(policy, times(1)).search(testUser);
    }

    @Test
    @DisplayName("Should create cafe successfully")
    void shouldCreateCafeSuccessfully() {
        // Given
        CafeCreateDto createDto = new CafeCreateDto();
        CafeDto cafeDto = new CafeDto();
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("admin", "password", new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("admin")).thenReturn(Optional.of(testUser));
        when(mapper.map(createDto)).thenReturn(testCafe);
        when(repository.save(testCafe)).thenReturn(testCafe);
        when(mapper.map(testCafe)).thenReturn(cafeDto);

        // When
        CafeDto result = cafeService.create(createDto);

        // Then
        assertEquals(cafeDto, result);
        verify(policy, times(1)).create(testUser);
        verify(repository, times(1)).save(testCafe);
    }

    @Test
    @DisplayName("Should get cafe by ID successfully")
    void shouldGetCafeByIdSuccessfully() {
        // Given
        CafeDto cafeDto = new CafeDto();
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("user", "password", new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("user")).thenReturn(Optional.of(testUser));
        when(repository.findById(1)).thenReturn(Optional.of(testCafe));
        when(mapper.map(testCafe)).thenReturn(cafeDto);

        // When
        CafeDto result = cafeService.getById(1);

        // Then
        assertEquals(cafeDto, result);
        verify(policy, times(1)).show(testUser, testCafe);
    }

    @Test
    @DisplayName("Should throw exception when cafe not found by ID")
    void shouldThrowExceptionWhenCafeNotFoundById() {
        // Given
        when(repository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            cafeService.getById(999);
        });
    }

    @Test
    @DisplayName("Should update cafe successfully")
    void shouldUpdateCafeSuccessfully() {
        // Given
        CafeUpdateDto updateDto = new CafeUpdateDto();
        CafeDto cafeDto = new CafeDto();
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("admin", "password", new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("admin")).thenReturn(Optional.of(testUser));
        when(repository.findById(1)).thenReturn(Optional.of(testCafe));
        when(mapper.update(updateDto, testCafe)).thenReturn(testCafe);
        when(repository.save(testCafe)).thenReturn(testCafe);
        when(mapper.map(testCafe)).thenReturn(cafeDto);

        // When
        CafeDto result = cafeService.update(updateDto, 1);

        // Then
        assertEquals(cafeDto, result);
        verify(policy, times(1)).update(testUser, testCafe);
        verify(mapper, times(1)).update(updateDto, testCafe);
        verify(repository, times(1)).save(testCafe);
    }

    @Test
    @DisplayName("Should throw exception when cafe to update not found")
    void shouldThrowExceptionWhenCafeToUpdateNotFound() {
        // Given
        CafeUpdateDto updateDto = new CafeUpdateDto();
        when(repository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            cafeService.update(updateDto, 999);
        });
    }

    @Test
    @DisplayName("Should delete cafe successfully")
    void shouldDeleteCafeSuccessfully() {
        // Given
        when(repository.findById(1)).thenReturn(Optional.of(testCafe));
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("admin", "password", new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("admin")).thenReturn(Optional.of(testUser));

        // When
        boolean result = cafeService.delete(1);

        // Then
        assertTrue(result);
        verify(policy, times(1)).delete(testUser, testCafe);
        verify(repository, times(1)).delete(testCafe);
    }

    @Test
    @DisplayName("Should return false when cafe to delete not found")
    void shouldReturnFalseWhenCafeToDeleteNotFound() {
        // Given
        when(repository.findById(999)).thenReturn(Optional.empty());

        // When
        boolean result = cafeService.delete(999);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should delete cafe with poster successfully")
    void shouldDeleteCafeWithPosterSuccessfully() {
        // Given
        Cafe cafeWithPoster = Cafe.builder()
                .id(1)
                .name("Test Cafe")
                .poster("poster.jpg")
                .build();
        
        when(repository.findById(1)).thenReturn(Optional.of(cafeWithPoster));
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("admin", "password", new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("admin")).thenReturn(Optional.of(testUser));

        // When
        boolean result = cafeService.delete(1);

        // Then
        assertTrue(result);
        verify(policy, times(1)).delete(testUser, cafeWithPoster);
        verify(storageService, times(1)).delete("poster.jpg");
        verify(repository, times(1)).delete(cafeWithPoster);
    }
}
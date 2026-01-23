package ru.ifmo.is.together.game;

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

import ru.ifmo.is.together.cafe.Cafe;
import ru.ifmo.is.together.common.errors.PolicyViolationError;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.search.SearchDto;
import ru.ifmo.is.together.common.search.SearchMapper;
import ru.ifmo.is.together.game.dto.GameCreateDto;
import ru.ifmo.is.together.game.dto.GameDto;
import ru.ifmo.is.together.users.User;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameMapper mapper;

    @Mock
    private GamePolicy policy;

    @Mock
    private GameRepository repository;

    @Mock
    private GameSpecification specification;

    @Mock
    private SearchMapper<Game> searchMapper;

    @InjectMocks
    private GameService gameService;

    private Game testGame;
    private Cafe testCafe;
    private User testUser;
    private User ownerUser;

    @BeforeEach
    void setUp() {
        testGame = Game.builder()
                .id(1)
                .name("Test Game")
                .build();

        testCafe = Cafe.builder()
                .id(1)
                .name("Test Cafe")
                .build();

        testUser = User.builder()
                .id(1)
                .username("testuser")
                .build();
                
        ownerUser = User.builder()
                .id(2)
                .username("owner")
                .build();
    }

    @Test
    @DisplayName("Should get cafe games successfully")
    void shouldGetCafeGamesSuccessfully() {
        // Given
        Pageable pageable = Pageable.unpaged();
        Page<Game> gamePage = new PageImpl<>(java.util.List.of(testGame));
        GameDto gameDto = new GameDto();
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("user", "password", new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("user")).thenReturn(Optional.of(testUser));
        when(specification.withCafe(1)).thenReturn(null);
        when(repository.findAll(any(), eq(pageable))).thenReturn(gamePage);
        when(mapper.map(testGame)).thenReturn(gameDto);

        // When
        Page<GameDto> result = gameService.getCafeFood(testCafe, pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(gameDto, result.getContent().get(0));
        verify(policy, times(1)).showAll(testUser);
    }

    @Test
    @DisplayName("Should find games by search criteria and cafe successfully")
    void shouldFindGamesBySearchCriteriaAndCafeSuccessfully() {
        // Given
        Pageable pageable = Pageable.unpaged();
        Page<Game> gamePage = new PageImpl<>(java.util.List.of(testGame));
        GameDto gameDto = new GameDto();
        SearchDto searchDto = new SearchDto();
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("user", "password", new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("user")).thenReturn(Optional.of(testUser));
        when(searchMapper.map(searchDto)).thenReturn(null);
        when(repository.findAll(any(), eq(pageable))).thenReturn(gamePage);
        when(mapper.map(testGame)).thenReturn(gameDto);

        // When
        Page<GameDto> result = gameService.findBySearchCriteriaAndCafe(searchDto, testCafe, pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(gameDto, result.getContent().get(0));
        verify(policy, times(1)).search(testUser);
    }

    @Test
    @DisplayName("Should create game successfully when user is cafe owner")
    void shouldCreateGameSuccessfullyWhenUserIsCafeOwner() {
        // Given
        GameCreateDto createDto = new GameCreateDto();
        GameDto gameDto = new GameDto();
        
        testCafe.setOwner(ownerUser);
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("owner", "password", new SimpleGrantedAuthority("ROLE_OWNER")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("owner")).thenReturn(Optional.of(ownerUser));
        when(mapper.map(createDto)).thenReturn(testGame);
        when(repository.save(testGame)).thenReturn(testGame);
        when(mapper.map(testGame)).thenReturn(gameDto);

        // When
        GameDto result = gameService.create(createDto, testCafe);

        // Then
        assertEquals(gameDto, result);
        verify(policy, times(1)).create(ownerUser);
        verify(repository, times(1)).save(testGame);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when user is not cafe owner")
    void shouldThrowAccessDeniedExceptionWhenUserIsNotCafeOwner() {
        // Given
        GameCreateDto createDto = new GameCreateDto();
        
        testCafe.setOwner(ownerUser);
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("otheruser", "password", new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("otheruser")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            gameService.create(createDto, testCafe);
        });
    }

    @Test
    @DisplayName("Should delete game successfully")
    void shouldDeleteGameSuccessfully() {
        // Given
        when(repository.findById(1)).thenReturn(Optional.of(testGame));
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("admin", "password", new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);
        
        when(repository.findByUsername("admin")).thenReturn(Optional.of(testUser));

        // When
        boolean result = gameService.delete(1);

        // Then
        assertTrue(result);
        verify(policy, times(1)).delete(testUser, testGame);
        verify(repository, times(1)).delete(testGame);
    }

    @Test
    @DisplayName("Should return false when game to delete not found")
    void shouldReturnFalseWhenGameToDeleteNotFound() {
        // Given
        when(repository.findById(999)).thenReturn(Optional.empty());

        // When
        boolean result = gameService.delete(999);

        // Then
        assertFalse(result);
    }
}
package ru.ifmo.is.together.refreshtokens;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.errors.TokenExpiredException;
import ru.ifmo.is.together.users.User;
import ru.ifmo.is.together.users.UserRepository;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1)
                .username("testuser")
                .build();
    }

    @Test
    @DisplayName("Should find token by token string successfully")
    void shouldFindTokenByTokenStringSuccessfully() {
        // Given
        String tokenString = "test_token";
        RefreshToken token = new RefreshToken(tokenString);
        token.setUser(testUser);
        when(repository.findByToken(tokenString)).thenReturn(Optional.of(token));

        // When
        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenString);

        // Then
        assertTrue(result.isPresent());
        assertEquals(token, result.get());
    }

    @Test
    @DisplayName("Should return empty optional when token not found by token string")
    void shouldReturnEmptyOptionalWhenTokenNotFoundByTokenString() {
        // Given
        when(repository.findByToken("invalid_token")).thenReturn(Optional.empty());

        // When
        Optional<RefreshToken> result = refreshTokenService.findByToken("invalid_token");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find last token for user successfully")
    void shouldFindLastTokenForUserSuccessfully() {
        // Given
        RefreshToken token = new RefreshToken("token");
        token.setUser(testUser);
        when(repository.findTopByUserOrderByExpiryDateDesc(testUser)).thenReturn(Optional.of(token));

        // When
        Optional<RefreshToken> result = refreshTokenService.findLast(testUser);

        // Then
        assertTrue(result.isPresent());
        assertEquals(token, result.get());
    }

    @Test
    @DisplayName("Should return empty optional when no token found for user")
    void shouldReturnEmptyOptionalWhenNoTokenFoundForUser() {
        // Given
        when(repository.findTopByUserOrderByExpiryDateDesc(testUser)).thenReturn(Optional.empty());

        // When
        Optional<RefreshToken> result = refreshTokenService.findLast(testUser);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should create refresh token successfully")
    void shouldCreateRefreshTokenSuccessfully() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(repository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        RefreshToken result = refreshTokenService.createRefreshToken(1);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertNotNull(result.getToken());
        assertNotNull(result.getExpiryDate());
        verify(repository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found during token creation")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFoundDuringTokenCreation() {
        // Given
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            refreshTokenService.createRefreshToken(999);
        });
    }

    @Test
    @DisplayName("Should verify expiration and return token when not expired")
    void shouldVerifyExpirationAndReturnTokenWhenNotExpired() {
        // Given
        Instant now = Instant.now();
        RefreshToken token = new RefreshToken("token");
        token.setUser(testUser);
        token.setExpiryDate(now.plusSeconds(3600)); // Expires in 1 hour

        // When
        RefreshToken result = refreshTokenService.verifyExpiration(token);

        // Then
        assertEquals(token, result);
        verify(repository, never()).delete(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Should throw TokenExpiredException when token is expired")
    void shouldThrowTokenExpiredExceptionWhenTokenIsExpired() {
        // Given
        Instant now = Instant.now();
        RefreshToken token = new RefreshToken("token");
        token.setUser(testUser);
        token.setExpiryDate(now.minusSeconds(1)); // Expired 1 second ago

        // When & Then
        assertThrows(TokenExpiredException.class, () -> {
            refreshTokenService.verifyExpiration(token);
        });
        verify(repository, times(1)).delete(token);
    }

    @Test
    @DisplayName("Should delete tokens by user ID successfully")
    void shouldDeleteTokensByUserIdSuccessfully() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        // When
        refreshTokenService.deleteByUserId(1);

        // Then
        verify(repository, times(1)).deleteByUser(testUser);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found during deletion")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFoundDuringDeletion() {
        // Given
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            refreshTokenService.deleteByUserId(999);
        });
    }
}
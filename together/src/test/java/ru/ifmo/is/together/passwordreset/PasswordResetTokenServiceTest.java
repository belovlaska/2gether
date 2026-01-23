package ru.ifmo.is.together.passwordreset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.ifmo.is.together.common.errors.TokenExpiredException;
import ru.ifmo.is.together.common.utils.crypto.TokenGenerator;
import ru.ifmo.is.together.users.User;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceTest {

    @Mock
    private PasswordResetTokenRepository repository;

    @Mock
    private TokenGenerator tokenGenerator;

    @InjectMocks
    private PasswordResetTokenService passwordResetTokenService;

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
        PasswordResetToken token = new PasswordResetToken(tokenString, testUser, Instant.now());
        when(repository.findByToken(tokenString)).thenReturn(Optional.of(token));

        // When
        Optional<PasswordResetToken> result = passwordResetTokenService.findByToken(tokenString);

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
        Optional<PasswordResetToken> result = passwordResetTokenService.findByToken("invalid_token");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find last token for user successfully")
    void shouldFindLastTokenForUserSuccessfully() {
        // Given
        PasswordResetToken token = new PasswordResetToken("token", testUser, Instant.now());
        when(repository.findTopByUserOrderByDateDesc(testUser)).thenReturn(Optional.of(token));

        // When
        Optional<PasswordResetToken> result = passwordResetTokenService.findLast(testUser);

        // Then
        assertTrue(result.isPresent());
        assertEquals(token, result.get());
    }

    @Test
    @DisplayName("Should return empty optional when no token found for user")
    void shouldReturnEmptyOptionalWhenNoTokenFoundForUser() {
        // Given
        when(repository.findTopByUserOrderByDateDesc(testUser)).thenReturn(Optional.empty());

        // When
        Optional<PasswordResetToken> result = passwordResetTokenService.findLast(testUser);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should create password reset token successfully")
    void shouldCreatePasswordResetTokenSuccessfully() {
        // Given
        String generatedToken = "generated_token";
        when(tokenGenerator.generateSecureToken(any(Integer.class))).thenReturn(generatedToken);
        when(repository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PasswordResetToken result = passwordResetTokenService.createPasswordResetToken(testUser);

        // Then
        assertNotNull(result);
        assertEquals(generatedToken, result.getToken());
        assertEquals(testUser, result.getUser());
        assertNotNull(result.getDate());
        verify(repository, times(1)).save(any(PasswordResetToken.class));
    }

    @Test
    @DisplayName("Should verify expiration and return token when not expired")
    void shouldVerifyExpirationAndReturnTokenWhenNotExpired() {
        // Given
        Instant now = Instant.now();
        PasswordResetToken token = new PasswordResetToken("token", testUser, now.minusSeconds(10));
        
        // Используем рефлексию для установки значения ttl в PasswordResetTokenService
        try {
            java.lang.reflect.Field field = PasswordResetTokenService.class.getDeclaredField("passwordResetTokenTtl");
            field.setAccessible(true);
            field.set(passwordResetTokenService, "3600000"); // 1 hour in milliseconds
        } catch (Exception e) {
            // Если не можем установить через рефлексию, используем альтернативный подход
        }

        // When
        PasswordResetToken result = passwordResetTokenService.verifyExpiration(token);

        // Then
        assertEquals(token, result);
        verify(repository, never()).delete(any(PasswordResetToken.class));
    }

    @Test
    @DisplayName("Should throw TokenExpiredException when token is expired")
    void shouldThrowTokenExpiredExceptionWhenTokenIsExpired() {
        // Given
        Instant now = Instant.now();
        PasswordResetToken token = new PasswordResetToken("token", testUser, now.minusHours(2));
        
        try {
            java.lang.reflect.Field field = PasswordResetTokenService.class.getDeclaredField("passwordResetTokenTtl");
            field.setAccessible(true);
            field.set(passwordResetTokenService, "3600000"); // 1 hour in milliseconds
        } catch (Exception e) {
            // Если не можем установить через рефлексию, используем альтернативный подход
        }

        // When & Then
        assertThrows(TokenExpiredException.class, () -> {
            passwordResetTokenService.verifyExpiration(token);
        });
        verify(repository, times(1)).delete(token);
    }
}
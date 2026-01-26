package ru.ifmo.is.together.verificationtokens;

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
class VerificationTokenServiceTest {

    @Mock
    private VerificationTokenRepository repository;

    @Mock
    private TokenGenerator tokenGenerator;

    @InjectMocks
    private VerificationTokenService verificationTokenService;

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
        VerificationToken token = new VerificationToken(tokenString, testUser, Instant.now());
        when(repository.findByToken(tokenString)).thenReturn(Optional.of(token));

        // When
        Optional<VerificationToken> result = verificationTokenService.findByToken(tokenString);

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
        Optional<VerificationToken> result = verificationTokenService.findByToken("invalid_token");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find last verification token for user successfully")
    void shouldFindLastVerificationTokenForUserSuccessfully() {
        // Given
        VerificationToken token = new VerificationToken("token", testUser, Instant.now());
        when(repository.findTopByUserOrderBySentAtDesc(testUser)).thenReturn(Optional.of(token));

        // When
        Optional<VerificationToken> result = verificationTokenService.findLastVerificationToken(testUser);

        // Then
        assertTrue(result.isPresent());
        assertEquals(token, result.get());
    }

    @Test
    @DisplayName("Should return empty optional when no verification token found for user")
    void shouldReturnEmptyOptionalWhenNoVerificationTokenFoundForUser() {
        // Given
        when(repository.findTopByUserOrderBySentAtDesc(testUser)).thenReturn(Optional.empty());

        // When
        Optional<VerificationToken> result = verificationTokenService.findLastVerificationToken(testUser);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should create verification token successfully")
    void shouldCreateVerificationTokenSuccessfully() {
        // Given
        String generatedToken = "generated_token";
        when(tokenGenerator.generateSecureToken(any(Integer.class))).thenReturn(generatedToken);
        when(repository.save(any(VerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        VerificationToken result = verificationTokenService.createVerificationToken(testUser);

        // Then
        assertNotNull(result);
        assertEquals(generatedToken, result.getToken());
        assertEquals(testUser, result.getUser());
        assertNotNull(result.getSentAt());
        verify(repository, times(1)).save(any(VerificationToken.class));
    }

    @Test
    @DisplayName("Should verify expiration and return token when not expired")
    void shouldVerifyExpirationAndReturnTokenWhenNotExpired() {
        // Given
        Instant now = Instant.now();
        VerificationToken token = new VerificationToken("token", testUser, now);
        token.setExpiryDate(now.plusSeconds(3600)); // Expires in 1 hour

        // When
        VerificationToken result = verificationTokenService.verifyExpiration(token);

        // Then
        assertEquals(token, result);
        verify(repository, never()).delete(any(VerificationToken.class));
    }

    @Test
    @DisplayName("Should throw TokenExpiredException when token is expired")
    void shouldThrowTokenExpiredExceptionWhenTokenIsExpired() {
        // Given
        Instant now = Instant.now();
        VerificationToken token = new VerificationToken("token", testUser, now);
        token.setExpiryDate(now.minusSeconds(1)); // Expired 1 second ago

        // When & Then
        assertThrows(TokenExpiredException.class, () -> {
            verificationTokenService.verifyExpiration(token);
        });
        verify(repository, times(1)).delete(token);
    }

    @Test
    @DisplayName("Should delete tokens by user successfully")
    void shouldDeleteTokensByUserSuccessfully() {
        // When
        verificationTokenService.deleteByUser(testUser);

        // Then
        verify(repository, times(1)).deleteByUser(testUser);
    }
}
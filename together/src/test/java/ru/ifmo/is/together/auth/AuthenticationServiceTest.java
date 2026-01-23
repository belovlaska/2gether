package ru.ifmo.is.together.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.ifmo.is.together.auth.dto.AuthenticationDto;
import ru.ifmo.is.together.auth.dto.SignInDto;
import ru.ifmo.is.together.auth.dto.SignUpDto;
import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.errors.TokenExpiredException;
import ru.ifmo.is.together.common.errors.TooManyRequests;
import ru.ifmo.is.together.common.errors.UserAlreadyConfirmedException;
import ru.ifmo.is.together.passwordreset.PasswordResetToken;
import ru.ifmo.is.together.passwordreset.PasswordResetTokenService;
import ru.ifmo.is.together.passwordreset.dto.RequestPasswordResetDto;
import ru.ifmo.is.together.passwordreset.dto.ResetPasswordDto;
import ru.ifmo.is.together.refreshtokens.RefreshToken;
import ru.ifmo.is.together.refreshtokens.RefreshTokenService;
import ru.ifmo.is.together.refreshtokens.dto.RefreshDto;
import ru.ifmo.is.together.users.User;
import ru.ifmo.is.together.users.UserMapper;
import ru.ifmo.is.together.users.UserService;
import ru.ifmo.is.together.verificationtokens.VerificationToken;
import ru.ifmo.is.together.verificationtokens.VerificationTokenService;
import ru.ifmo.is.together.verificationtokens.dto.VerificationDto;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper mapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshService;

    @Mock
    private VerificationTokenService verificationService;

    @Mock
    private PasswordResetTokenService passwordResetService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;
    private SignUpDto signUpDto;
    private SignInDto signInDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1)
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .roles(new HashSet<>())
                .build();

        signUpDto = new SignUpDto();
        signUpDto.setUsername("newuser");
        signUpDto.setEmail("newuser@example.com");
        signUpDto.setPassword("password");

        signInDto = new SignInDto();
        signInDto.setUsername("testuser");
        signInDto.setPassword("password");
    }

    @Test
    @DisplayName("Should get current user info successfully")
    void shouldGetMeSuccessfully() {
        // Given
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("testuser", "password", new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(refreshService.findLast(testUser)).thenReturn(Optional.of(new RefreshToken()));
        when(jwtService.generateToken(testUser)).thenReturn("jwt_token");
        when(mapper.map(testUser)).thenReturn(new ru.ifmo.is.together.users.dto.UserDto());

        // When
        AuthenticationDto result = authenticationService.me();

        // Then
        assertNotNull(result);
        assertEquals("jwt_token", result.getAccessToken());
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldSignUpSuccessfully() {
        // Given
        when(passwordEncoder.encode("password")).thenReturn("encoded_password");
        when(userService.create(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn("jwt_token");
        when(refreshService.createRefreshToken(1)).thenReturn(new RefreshToken("refresh_token"));

        // When
        AuthenticationDto result = authenticationService.signUp(signUpDto);

        // Then
        assertNotNull(result);
        assertEquals("jwt_token", result.getAccessToken());
        assertEquals("refresh_token", result.getRefreshToken());
    }

    @Test
    @DisplayName("Should authenticate user successfully")
    void shouldSignInSuccessfully() {
        // Given
        UserDetails userDetails = mock(UserDetails.class);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userService.userDetailsService()).thenReturn(username -> userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.getByUsername("testuser")).thenReturn(testUser);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt_token");
        when(refreshService.createRefreshToken(1)).thenReturn(new RefreshToken("refresh_token"));
        when(mapper.map(testUser)).thenReturn(new ru.ifmo.is.together.users.dto.UserDto());

        // When
        AuthenticationDto result = authenticationService.signIn(signInDto);

        // Then
        assertNotNull(result);
        assertEquals("jwt_token", result.getAccessToken());
        assertEquals("refresh_token", result.getRefreshToken());
    }

    @Test
    @DisplayName("Should refresh tokens successfully")
    void shouldRefreshTokensSuccessfully() {
        // Given
        RefreshToken refreshToken = new RefreshToken("old_refresh_token");
        refreshToken.setUser(testUser);
        when(refreshService.findByToken("old_refresh_token")).thenReturn(Optional.of(refreshToken));
        when(refreshService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(jwtService.generateToken(testUser)).thenReturn("new_jwt_token");
        when(refreshService.createRefreshToken(1)).thenReturn(new RefreshToken("new_refresh_token"));
        when(mapper.map(testUser)).thenReturn(new ru.ifmo.is.together.users.dto.UserDto());

        RefreshDto refreshDto = new RefreshDto();
        refreshDto.setRefreshToken("old_refresh_token");

        // When
        AuthenticationDto result = authenticationService.refresh(refreshDto);

        // Then
        assertNotNull(result);
        assertEquals("new_jwt_token", result.getAccessToken());
        assertEquals("new_refresh_token", result.getRefreshToken());
    }

    @Test
    @DisplayName("Should throw exception when refresh token not found")
    void shouldThrowExceptionWhenRefreshTokenNotFound() {
        // Given
        when(refreshService.findByToken("invalid_token")).thenReturn(Optional.empty());

        RefreshDto refreshDto = new RefreshDto();
        refreshDto.setRefreshToken("invalid_token");

        // When & Then
        assertThrows(TokenExpiredException.class, () -> {
            authenticationService.refresh(refreshDto);
        });
    }

    @Test
    @DisplayName("Should confirm user successfully")
    void shouldConfirmUserSuccessfully() {
        // Given
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(testUser);
        when(verificationService.findByToken("valid_token")).thenReturn(Optional.of(verificationToken));
        when(verificationService.verifyExpiration(verificationToken)).thenReturn(verificationToken);
        when(userService.confirm(testUser)).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn("jwt_token");
        when(refreshService.createRefreshToken(1)).thenReturn(new RefreshToken("refresh_token"));
        when(mapper.map(testUser)).thenReturn(new ru.ifmo.is.together.users.dto.UserDto());

        VerificationDto verificationDto = new VerificationDto();
        verificationDto.setVerificationToken("valid_token");

        // When
        AuthenticationDto result = authenticationService.confirm(verificationDto);

        // Then
        assertNotNull(result);
        assertEquals("jwt_token", result.getAccessToken());
        assertEquals("refresh_token", result.getRefreshToken());
    }

    @Test
    @DisplayName("Should throw exception when verification token not found")
    void shouldThrowExceptionWhenVerificationTokenNotFound() {
        // Given
        when(verificationService.findByToken("invalid_token")).thenReturn(Optional.empty());

        VerificationDto verificationDto = new VerificationDto();
        verificationDto.setVerificationToken("invalid_token");

        // When & Then
        assertThrows(TokenExpiredException.class, () -> {
            authenticationService.confirm(verificationDto);
        });
    }

    @Test
    @DisplayName("Should reset password successfully")
    void shouldResetPasswordSuccessfully() {
        // Given
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(testUser);
        when(passwordResetService.findByToken("valid_token")).thenReturn(Optional.of(passwordResetToken));
        when(passwordResetService.verifyExpiration(passwordResetToken)).thenReturn(passwordResetToken);
        when(passwordEncoder.encode("new_password")).thenReturn("encoded_new_password");
        when(userService.save(testUser)).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn("jwt_token");
        when(refreshService.createRefreshToken(1)).thenReturn(new RefreshToken("refresh_token"));
        when(mapper.map(testUser)).thenReturn(new ru.ifmo.is.together.users.dto.UserDto());

        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setPasswordResetToken("valid_token");
        resetPasswordDto.setPassword("new_password");

        // When
        AuthenticationDto result = authenticationService.resetPassword(resetPasswordDto);

        // Then
        assertNotNull(result);
        assertEquals("jwt_token", result.getAccessToken());
        assertEquals("refresh_token", result.getRefreshToken());
    }

    @Test
    @DisplayName("Should throw exception when password reset token not found")
    void shouldThrowExceptionWhenPasswordResetTokenNotFound() {
        // Given
        when(passwordResetService.findByToken("invalid_token")).thenReturn(Optional.empty());

        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setPasswordResetToken("invalid_token");
        resetPasswordDto.setPassword("new_password");

        // When & Then
        assertThrows(TokenExpiredException.class, () -> {
            authenticationService.resetPassword(resetPasswordDto);
        });
    }

    @Test
    @DisplayName("Should resend confirmation successfully")
    void shouldResendConfirmationSuccessfully() {
        // Given
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("testuser", "password", new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(verificationService.findLastVerificationToken(testUser))
            .thenReturn(Optional.of(new VerificationToken("token", testUser, Instant.now().minusSeconds(120))));
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        authenticationService.resendConfirmation();

        // Then
        verify(userService, times(1)).sendConfirmation(eq(testUser), eq(true));
    }

    @Test
    @DisplayName("Should throw exception when user already confirmed")
    void shouldThrowExceptionWhenUserAlreadyConfirmed() {
        // Given
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("confirmed_user", "password", new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        User confirmedUser = User.builder()
            .id(1)
            .username("confirmed_user")
            .email("confirmed@example.com")
            .password("encoded_password")
            .build();
        confirmedUser.setRoles(new HashSet<>());
        confirmedUser.getRoles().add(ru.ifmo.is.together.userroles.UserRole.builder()
            .role(ru.ifmo.is.together.userroles.Role.ROLE_USER).build());

        when(userService.getCurrentUser()).thenReturn(confirmedUser);

        // When & Then
        assertThrows(UserAlreadyConfirmedException.class, () -> {
            authenticationService.resendConfirmation();
        });
    }

    @Test
    @DisplayName("Should request password reset successfully")
    void shouldRequestPasswordResetSuccessfully() {
        // Given
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordResetService.findLast(testUser)).thenReturn(Optional.empty());
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        RequestPasswordResetDto request = new RequestPasswordResetDto();
        request.setEmail("test@example.com");

        // When
        authenticationService.requestPasswordReset(request);

        // Then
        verify(userService, times(1)).sendPasswordReset(testUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found during password reset request")
    void shouldThrowExceptionWhenUserNotFoundDuringPasswordResetRequest() {
        // Given
        when(userService.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        RequestPasswordResetDto request = new RequestPasswordResetDto();
        request.setEmail("nonexistent@example.com");

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            authenticationService.requestPasswordReset(request);
        });
    }

    @Test
    @DisplayName("Should sign out successfully")
    void shouldSignOutSuccessfully() {
        // Given
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
            new TestingAuthenticationToken("testuser", "password", new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        when(userService.getCurrentUser()).thenReturn(testUser);

        // When
        authenticationService.signOut(true);

        // Then
        verify(refreshService, times(1)).deleteByUserId(1);
    }
}
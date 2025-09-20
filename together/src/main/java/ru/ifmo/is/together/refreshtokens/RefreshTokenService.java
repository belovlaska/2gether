package ru.ifmo.is.together.refreshtokens;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.ifmo.is.together.common.errors.ResourceNotFoundException;
import ru.ifmo.is.together.common.errors.TokenExpiredException;
import ru.ifmo.is.together.users.User;
import ru.ifmo.is.together.users.UserRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  @Value("${app.jwt.token.refresh.expiration}")
  private String jwtRefreshExpirationTime;

  private final RefreshTokenRepository repository;
  private final UserRepository userRepository;

  public Optional<RefreshToken> findByToken(String token) {
    return repository.findByToken(token);
  }

  public Optional<RefreshToken> findLast(User user) {
    return repository.findTopByUserOrderByExpiryDateDesc(user);
  }

  public RefreshToken createRefreshToken(int userId) {
    var user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + userId));
    var refreshToken = RefreshToken.builder()
      .user(user)
      .expiryDate(Instant.now().plusMillis(Long.parseLong(jwtRefreshExpirationTime)))
      .token(UUID.randomUUID().toString())
      .build();

    return repository.save(refreshToken);
  }

  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      repository.delete(token);
      throw new TokenExpiredException("Refresh token was expired. Please make a new signin request");
    }

    return token;
  }

  @Transactional
  public void deleteByUserId(int userId) {
    var user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + userId));

    repository.deleteByUser(user);
  }
}

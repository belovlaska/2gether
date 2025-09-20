package ru.ifmo.is.together.verificationtokens;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.ifmo.is.together.common.errors.TokenExpiredException;
import ru.ifmo.is.together.common.utils.crypto.TokenGenerator;
import ru.ifmo.is.together.users.User;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

  @Value("${app.auth.verification_token.ttl}")
  private String verificationTokenTtl;

  @Value("${app.auth.verification_token.length}")
  private String verificationTokenLength;

  private final VerificationTokenRepository repository;
  private final TokenGenerator tokenGenerator;

  public Optional<VerificationToken> findByToken(String token) {
    return repository.findByToken(token);
  }

  public Optional<VerificationToken> findLastVerificationToken(User user) {
    return repository.findTopByUserOrderBySentAtDesc(user);
  }

  public VerificationToken createVerificationToken(User user) {
    var token = tokenGenerator.generateSecureToken(Integer.parseInt(verificationTokenLength));
    var verificationToken = VerificationToken.builder()
      .user(user)
      .sentAt(Instant.now())
      .expiryDate(Instant.now().plusMillis(Long.parseLong(verificationTokenTtl)))
      .token(token)
      .build();

    return repository.save(verificationToken);
  }

  public VerificationToken verifyExpiration(VerificationToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      repository.delete(token);
      throw new TokenExpiredException("Verification token was expired. Please make a new confirmation request");
    }

    return token;
  }

  @Transactional
  public void deleteByUser(User user) {
    repository.deleteByUser(user);
  }
}

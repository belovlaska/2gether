package ru.ifmo.is.together.common.utils.crypto;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenGenerator {
  public String generateSecureToken(int tokenLength) {
    var random = new SecureRandom();
    byte[] bytes = new byte[tokenLength];
    random.nextBytes(bytes);
    var encoder = Base64.getUrlEncoder().withoutPadding();
    return encoder.encodeToString(bytes).substring(0, tokenLength);
  }
}

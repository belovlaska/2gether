package ru.ifmo.is.together.common.errors;

public class TokenExpiredException extends RuntimeException {
  public TokenExpiredException() {
    super();
  }

  public TokenExpiredException(String message) {
    super(message);
  }
}

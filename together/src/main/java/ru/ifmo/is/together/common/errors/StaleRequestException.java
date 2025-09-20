package ru.ifmo.is.together.common.errors;

public class StaleRequestException extends RuntimeException {
  public StaleRequestException() {
    super();
  }

  public StaleRequestException(String message) {
    super(message);
  }
}

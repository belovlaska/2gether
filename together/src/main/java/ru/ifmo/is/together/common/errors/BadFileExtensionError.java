package ru.ifmo.is.together.common.errors;

public class BadFileExtensionError extends RuntimeException {
  public BadFileExtensionError() {
    super();
  }

  public BadFileExtensionError(String message) {
    super(message);
  }
}

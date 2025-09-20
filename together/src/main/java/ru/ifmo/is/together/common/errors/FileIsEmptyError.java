package ru.ifmo.is.together.common.errors;

public class FileIsEmptyError extends RuntimeException {
  public FileIsEmptyError() {
    super();
  }

  public FileIsEmptyError(String message) {
    super(message);
  }
}

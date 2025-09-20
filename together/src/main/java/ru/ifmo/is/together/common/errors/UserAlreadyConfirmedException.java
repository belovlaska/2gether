package ru.ifmo.is.together.common.errors;

public class UserAlreadyConfirmedException extends RuntimeException {
  public UserAlreadyConfirmedException() {
    super();
  }

  public UserAlreadyConfirmedException(String message) {
    super(message);
  }
}

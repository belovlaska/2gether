package ru.ifmo.is.together.common.errors;

public class ResourceAlreadyExists extends RuntimeException {
  public ResourceAlreadyExists() {
    super();
  }

  public ResourceAlreadyExists(String message) {
    super(message);
  }
}

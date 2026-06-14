package com.fitness.aiservice.exceptions;

public class ActivityNotFoundException extends RuntimeException {
  public ActivityNotFoundException(String message) {
    super(message);
  }
}

package ru.sf.personalfinancemanagementsystem.exceptions;

public class BadPasswordException extends RuntimeException {
  public BadPasswordException(String message) {
    super(message);
  }
}

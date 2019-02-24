package com.andrew.exception;

/**
 * @author andrew
 */
public class InsufficientBalanceException extends RuntimeException {

  public InsufficientBalanceException(String message) {
    super(message);
  }
}
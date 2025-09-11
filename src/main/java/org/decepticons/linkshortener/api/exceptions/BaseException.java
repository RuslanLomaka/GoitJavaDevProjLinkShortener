package org.decepticons.linkshortener.api.exceptions;

/**
 * Base class for custom exceptions in the application.
 * Helps unify exception handling.
 */
public abstract class BaseException extends RuntimeException {

  /**
   * Constructs a new BaseException with the specified detail message.
   *
   * @param message the detail message.
   */
  protected BaseException(final String message) {
    super(message);
  }

  /**
   * Constructs a new BaseException with the specified detail message
   * and cause.
   *
   * @param message the detail message.
   * @param cause the cause of this exception.
   */
  protected BaseException(final String message, final Throwable cause) {
    super(message, cause);
  }
}

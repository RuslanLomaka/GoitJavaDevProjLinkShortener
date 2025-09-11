package org.decepticons.linkshortener.api.exceptions;

/**
 * Thrown when a JWT token is missing, malformed, or invalid.
 */
public class InvalidTokenException extends BaseException {

  /**
   * Constructs a new InvalidTokenException with the specified detail message.
   *
   * @param message the detail message.
   */
  public InvalidTokenException(final String message) {
    super(message);
  }

  /**
   * Constructs a new InvalidTokenException with
   * the specified detail message and cause.
   *
   * @param message the detail message.
   * @param cause the cause of the exception.
   */

  public InvalidTokenException(final String message, final Throwable cause) {
    super(message, cause);
  }
}

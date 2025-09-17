package org.decepticons.linkshortener.api.exception;


/**
 * Thrown when a JWT token is expired.
 */
public class ExpiredTokenException extends BaseException {

  /**
   * Constructs a new ExpiredTokenException with the specified detail message.
   *
   * @param message the detail message.
   */
  public ExpiredTokenException(final String message) {
    super(message);
  }

  /**
   * Constructs a new ExpiredTokenException with the specified detail message
   * and cause.
   *
   * @param message the detail message.
   * @param cause the cause of this exception.
   */
  public ExpiredTokenException(final String message, final Throwable cause) {
    super(message, cause);
  }
}

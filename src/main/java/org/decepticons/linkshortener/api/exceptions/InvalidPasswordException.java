package org.decepticons.linkshortener.api.exceptions;

/**
 * Thrown when a password does not meet complexity requirements.
 */
public class InvalidPasswordException extends BaseException {
  /**
   * Constructs a new InvalidPasswordException with
   * the specified detail message.
   *
   * @param message the detail message.
   */
  public InvalidPasswordException(final String message) {
    super(message);
  }
}

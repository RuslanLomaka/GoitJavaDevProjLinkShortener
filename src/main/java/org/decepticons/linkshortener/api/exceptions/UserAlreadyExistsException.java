package org.decepticons.linkshortener.api.exceptions;

/**
 * Thrown when trying to register a user with a username that already exists.
 */
public class UserAlreadyExistsException extends BaseException {

  /**
   * Constructs a new UserAlreadyExistsException with a detail message.
   *
   * @param username The username that already exists.
   */
  public UserAlreadyExistsException(final String username) {
    super("User with username '" + username + "' already exists");
  }
}

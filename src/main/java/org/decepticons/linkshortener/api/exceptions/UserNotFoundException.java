package org.decepticons.linkshortener.api.exceptions;

/**
 * Thrown when a user is not found in the system.
 */
public class UserNotFoundException extends BaseException {

  /**
   * Constructs a new UserNotFoundException with a detail message.
   *
   * @param username The username that was not found.
   */
  public UserNotFoundException(final String username) {
    super("User with username '" + username + "' not found");
  }
}

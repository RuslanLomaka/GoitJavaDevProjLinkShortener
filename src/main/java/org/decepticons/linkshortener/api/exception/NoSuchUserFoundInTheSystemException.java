package org.decepticons.linkshortener.api.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Runtime exception thrown when a user is not found in the system.
 * Annotated with 404 Not Found for HTTP responses.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
@Setter
@Getter
public class NoSuchUserFoundInTheSystemException extends RuntimeException {

  private String username;

  /**
   * Constructs a new exception with a message and the username that was not found.
   */
  public NoSuchUserFoundInTheSystemException(String message, String username) {
    super(message);
    this.username = username;
  }
}

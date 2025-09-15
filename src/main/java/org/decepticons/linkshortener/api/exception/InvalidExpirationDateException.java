package org.decepticons.linkshortener.api.exception;

import java.time.Instant;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an invalid expiration date is provided for a link.
 * This exception indicates that the provided expiration date does not meet
 * the required criteria (e.g., it may be in the past or too far in the future).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class InvalidExpirationDateException extends RuntimeException {
  private final Instant invalidDate;

  /**
   * Constructs a new InvalidExpirationDateException with the specified detail message
   * and the invalid expiration date.
   *
   * @param message     the detail message.
   * @param invalidDate the invalid expiration date that caused this exception.
   */
  public InvalidExpirationDateException(String message, Instant invalidDate) {
    super(message);
    this.invalidDate = invalidDate;
  }

}

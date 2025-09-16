package org.decepticons.linkshortener.api.exception;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Runtime exception thrown when a short link has expired or is no longer valid.
 */
@ResponseStatus(HttpStatus.GONE)
@Getter
@Setter
public class ShortLinkIsOutOfDateException extends RuntimeException {

  private String shortLink;

  private Instant expiredAt;

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message the detail message
   */
  public ShortLinkIsOutOfDateException(String message, String shortLink, Instant expiredAt) {
    super(message);
    this.shortLink = shortLink;
    this.expiredAt = expiredAt;
  }
}

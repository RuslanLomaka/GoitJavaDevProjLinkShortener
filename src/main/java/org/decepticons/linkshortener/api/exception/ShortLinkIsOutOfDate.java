package org.decepticons.linkshortener.api.exception;


import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;

/**
 * Runtime exception thrown when a short link has expired or is no longer valid.
 */
@ResponseStatus(HttpStatus.GONE)
@Data
public class ShortLinkIsOutOfDate extends RuntimeException {

  private String shortLink;

  private Instant expiredAt;

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message the detail message
   */
  public ShortLinkIsOutOfDate(String message, String shortLink, Instant expiredAt) {
    super(message);
    this.shortLink = shortLink;
    this.expiredAt = expiredAt;
  }
}

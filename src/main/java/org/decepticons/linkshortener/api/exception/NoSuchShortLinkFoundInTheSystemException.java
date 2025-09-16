package org.decepticons.linkshortener.api.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Runtime exception thrown when a short link is not found in the system.
 * Annotated with 404 Not Found for HTTP responses.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
@Setter
@Getter
public class NoSuchShortLinkFoundInTheSystemException extends RuntimeException {

  private String shortLink;

  /**
   * Constructs a new exception with a message and the short link that was not found.
   */
  public NoSuchShortLinkFoundInTheSystemException(String message, String shortLink) {
    super(message);
    this.shortLink = shortLink;
  }

}

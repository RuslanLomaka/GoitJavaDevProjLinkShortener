package org.decepticons.linkshortener.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class InvalidExpirationDateException extends RuntimeException {
  private final Instant invalidDate;

  public InvalidExpirationDateException(String message, Instant invalidDate) {
    super(message);
    this.invalidDate = invalidDate;
  }

}

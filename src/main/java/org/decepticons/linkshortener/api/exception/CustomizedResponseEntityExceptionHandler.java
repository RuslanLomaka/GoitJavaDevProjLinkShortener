package org.decepticons.linkshortener.api.exception;

import org.decepticons.linkshortener.api.dto.NoSuchLinkFoundResponseDto;
import org.decepticons.linkshortener.api.dto.NoSuchUserFoundResponseDto;
import org.decepticons.linkshortener.api.dto.ShortLinkOutOfDateResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler for the application.
 * Handles custom exceptions and transforms them into appropriate HTTP responses.
 */
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handles the case when a user is not found in the system.
   * Returns a 404 Not Found response with a descriptive message.
   */
  @ExceptionHandler(NoSuchUserFoundInTheSystemException.class)
  public ResponseEntity<NoSuchUserFoundResponseDto> handleNoSuchUserFoundInTheSystem(
      NoSuchUserFoundInTheSystemException ex) {

    NoSuchUserFoundResponseDto response = new NoSuchUserFoundResponseDto(
        ex.getUsername(),
        "No such user found in the system"
    );

    return ResponseEntity.status(404).body(response);
  }


  /**
   * Handles the case when a short link is not found in the system.
   * Returns a 404 Not Found response with a descriptive message.
   */
  @ExceptionHandler(NoSuchShortLinkFoundInTheSystemException.class)
  public ResponseEntity<NoSuchLinkFoundResponseDto> handleNoSuchShortLinkFoundInTheSystem(
      NoSuchShortLinkFoundInTheSystemException ex) {

    NoSuchLinkFoundResponseDto response = new NoSuchLinkFoundResponseDto(
        ex.getShortLink(),
        "No such short link found in the system"
    );

    return ResponseEntity.status(404).body(response);
  }


  /**
   * Handles the case when a short link is out of date (expired).
   * Returns a 410 Gone response with details about the expired link.
   */
  @ExceptionHandler(ShortLinkIsOutOfDateException.class)
  public ResponseEntity<ShortLinkOutOfDateResponseDto> handleShortLinkIsOutOfDate(
      ShortLinkIsOutOfDateException ex
  ) {
    ShortLinkOutOfDateResponseDto response = new ShortLinkOutOfDateResponseDto(
        ex.getShortLink(),
        "The short link is out of date",
        ex.getExpiredAt()
    );

    return ResponseEntity.status(410).body(response);
  }


}

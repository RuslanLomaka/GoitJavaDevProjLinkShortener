package org.decepticons.linkshortener.api.exception;

import org.decepticons.linkshortener.api.dto.NoSuchUserFoundResponse;
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
  @ExceptionHandler(NoSuchUserFoundInTheSystem.class)
  public ResponseEntity<NoSuchUserFoundResponse> handleNoSuchUserFoundInTheSystem(
      NoSuchUserFoundInTheSystem ex) {

    NoSuchUserFoundResponse response = new NoSuchUserFoundResponse(
        ex.getUsername(),
        "No such user found in the system"
    );

    return ResponseEntity.status(404).body(response);
  }
}

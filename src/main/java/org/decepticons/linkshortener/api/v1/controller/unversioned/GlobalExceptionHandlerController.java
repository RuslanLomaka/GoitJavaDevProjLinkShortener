package org.decepticons.linkshortener.api.v1.controller.unversioned;

import java.time.Instant;
import java.util.Map;
import org.decepticons.linkshortener.api.exception.ExpiredTokenException;
import org.decepticons.linkshortener.api.exception.InvalidPasswordException;
import org.decepticons.linkshortener.api.exception.InvalidTokenException;
import org.decepticons.linkshortener.api.exception.NoSuchShortLinkFoundInTheSystemException;
import org.decepticons.linkshortener.api.exception.NoSuchUserFoundInTheSystemException;
import org.decepticons.linkshortener.api.exception.ShortLinkIsOutOfDateException;
import org.decepticons.linkshortener.api.exception.UserAlreadyExistsException;
import org.decepticons.linkshortener.api.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for managing application-wide exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandlerController {

  /**
   * Builds a standardized error response.
   *
   * @param status  The HTTP status to be returned.
   * @param error   A brief description of the error.
   * @param message A detailed message about the error.
   * @param details Additional details about the error.
   * @return A ResponseEntity containing the error details.
   */
  private ResponseEntity<Map<String, Object>> buildErrorResponse(
      final HttpStatus status,
      final String error,
      final String message,
      final Object details) {
    return ResponseEntity.status(status).body(Map.of(
        "timestamp", Instant.now().toString(),
        "status", status.value(),
        "error", error,
        "message", message,
        "details", details
    ));
  }

  /**
   * Builds a standardized error response.
   *
   * @param status  The HTTP status to be returned.
   * @param error   A brief description of the error.
   * @param message A detailed message about the error.
   * @return A ResponseEntity containing the error details.
   */

  private ResponseEntity<Map<String, Object>> buildErrorResponseSecurity(
      final HttpStatus status,
      final String error,
      final String message) {
    return ResponseEntity.status(status).body(Map.of(
        "timestamp", Instant.now().toString(),
        "status", status.value(),
        "error", error,
        "message", message
    ));
  }

  /**
   * Handles exceptions when a user already exists.
   *
   * @param ex The UserAlreadyExistsException instance.
   * @return A ResponseEntity with a CONFLICT status.
   */
  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<Map<String, Object>> handleUserExists(
      final UserAlreadyExistsException ex) {
    return buildErrorResponseSecurity(
        HttpStatus.CONFLICT,
        "User Already Exists",
        ex.getMessage());
  }

  /**
   * Handles exceptions when a user is not found.
   *
   * @param ex The UserNotFoundException instance.
   * @return A ResponseEntity with a NOT_FOUND status.
   */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleUserNotFound(
      final UserNotFoundException ex) {
    return buildErrorResponseSecurity(
        HttpStatus.NOT_FOUND,
        "User Not Found",
        ex.getMessage());
  }

  /**
   * Handles exceptions when an invalid password is provided.
   *
   * @param ex The InvalidPasswordException instance.
   * @return A ResponseEntity with a BAD_REQUEST status.
   */
  @ExceptionHandler(InvalidPasswordException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidPassword(
      final InvalidPasswordException ex) {
    return buildErrorResponseSecurity(
        HttpStatus.BAD_REQUEST,
        "Invalid Password",
        ex.getMessage());
  }

  /**
   * Handles exceptions for an invalid token.
   *
   * @param ex The InvalidTokenException instance.
   * @return A ResponseEntity with an UNAUTHORIZED status.
   */
  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidToken(
      final InvalidTokenException ex) {
    return buildErrorResponseSecurity(
        HttpStatus.UNAUTHORIZED,
        "Invalid Token",
        ex.getMessage());
  }

  /**
   * Handles exceptions for an expired token.
   *
   * @param ex The ExpiredTokenException instance.
   * @return A ResponseEntity with an UNAUTHORIZED status.
   */
  @ExceptionHandler(ExpiredTokenException.class)
  public ResponseEntity<Map<String, Object>> handleExpiredToken(
      final ExpiredTokenException ex) {
    return buildErrorResponseSecurity(HttpStatus.UNAUTHORIZED,
        "Expired Token",
        ex.getMessage());
  }

  /**
   * Handles generic exceptions.
   *
   * @param ex The generic Exception instance.
   * @return A ResponseEntity with an INTERNAL_SERVER_ERROR status.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneric(final Exception ex) {
    return buildErrorResponseSecurity(HttpStatus.INTERNAL_SERVER_ERROR,
        "Server Error",
        ex.getMessage());
  }

  /**
   * Handles exceptions when no such user is found in the system.
   *
   * @param ex the exception instance
   *
   * @return a ResponseEntity with error details
   */

  @ExceptionHandler(NoSuchUserFoundInTheSystemException.class)
  public ResponseEntity<Map<String, Object>> handleNoSuchUser(
      NoSuchUserFoundInTheSystemException ex) {

    return buildErrorResponse(
        HttpStatus.NOT_FOUND,
        "User Not Found",
        "No such user in the system",
        Map.of(
            "username", ex.getUsername()
        )
    );
  }

  /**
   * Handles exceptions when no such short link is found in the system.
   *
   * @param ex Exception instance
   *
   * @return Response entity with error details
   */

  @ExceptionHandler(NoSuchShortLinkFoundInTheSystemException.class)
  public ResponseEntity<Map<String, Object>> handleNoSuchLink(
      NoSuchShortLinkFoundInTheSystemException ex) {

    return buildErrorResponse(
        HttpStatus.NOT_FOUND,
        "Short Link Not Found",
        "No such short link in the system",
        Map.of(
            "shortLink", ex.getShortLink()
        )
    );
  }

  /**
   * Handles exceptions when a short link is out of date.
   *
   * @param ex Exception instance
   *
   * @return Response entity with error details
   */

  @ExceptionHandler(ShortLinkIsOutOfDateException.class)
  public ResponseEntity<Map<String, Object>> handleShortLinkOutOfDate(
      ShortLinkIsOutOfDateException ex) {
    return buildErrorResponse(
        HttpStatus.GONE,
        "Short Link Expired",
        "The short link is out of date",
        Map.of(
            "shortLink", ex.getShortLink(),
            "expiredAt", ex.getExpiredAt()
        )
    );
  }

  /**
   * Handles exceptions for bad login credentials.
   *
   * @param ex The BadCredentialsException instance.
   * @return A ResponseEntity with an UNAUTHORIZED status.
   */
  @ExceptionHandler(
          org.springframework.security.authentication
                  .BadCredentialsException.class
  )
  public ResponseEntity<Map<String, Object>> handleBadCredentials(
          final org.springframework.security.authentication
                  .BadCredentialsException ex
  ) {
    return buildErrorResponseSecurity(
            HttpStatus.UNAUTHORIZED,
            "Invalid Credentials",
            ex.getMessage());
  }

}

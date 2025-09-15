package org.decepticons.linkshortener.api.controller;

import java.time.Instant;
import java.util.Map;

import org.decepticons.linkshortener.api.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandlerController {

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

  // === Link Shortener специфичные ошибки ===
  @ExceptionHandler(NoSuchUserFoundInTheSystemException.class)
  public ResponseEntity<Map<String, Object>> handleNoSuchUser(NoSuchUserFoundInTheSystemException ex) {
    return buildErrorResponse(
        HttpStatus.NOT_FOUND,
        "User Not Found",
        "No such user in the system",
        Map.of("username", ex.getUsername())
    );
  }

  @ExceptionHandler(NoSuchShortLinkFoundInTheSystemException.class)
  public ResponseEntity<Map<String, Object>> handleNoSuchLink(NoSuchShortLinkFoundInTheSystemException ex) {
    return buildErrorResponse(
        HttpStatus.NOT_FOUND,
        "Short Link Not Found",
        "No such short link in the system",
        Map.of("shortLink", ex.getShortLink())
    );
  }

  @ExceptionHandler(ShortLinkIsOutOfDateException.class)
  public ResponseEntity<Map<String, Object>> handleShortLinkOutOfDate(ShortLinkIsOutOfDateException ex) {
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


}

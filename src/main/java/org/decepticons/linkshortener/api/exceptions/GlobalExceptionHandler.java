package org.decepticons.linkshortener.api.exceptions;

import io.jsonwebtoken.JwtException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Centralized exception handler for REST controllers.
 * This class catches specific exceptions and maps them
 * to appropriate HTTP status codes, ensuring a consistent and
 * informative error response
 * format.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Constructs a standard error response body.
   *
   * @param status  the HTTP status to return.
   * @param error   the short error description.
   * @param message the detailed error message.
   * @return a map representing the error response body.
   */
  private ResponseEntity<Map<String, Object>> buildErrorResponse(
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
   * Handles all authentication-related exceptions, including bad credentials.
   * This ensures that login failures and other authentication issues return a
   * consistent 401 Unauthorized status.
   *
   * @param ex The AuthenticationException instance.
   * @return A ResponseEntity with an UNAUTHORIZED status.
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, Object>> handleAuthenticationException(
      final AuthenticationException ex
  ) {
    return buildErrorResponse(
        HttpStatus.UNAUTHORIZED,
        "Authentication Failed",
        "Invalid username or password");
  }

  /**
   * Handles exceptions related to JWT parsing and validation.
   * This catches various JWT-specific issues that might not be a part of
   * your custom exceptions, such as signature validation
   * failures or malformed tokens.
   *
   * @param ex The JwtException instance.
   * @return A ResponseEntity with an UNAUTHORIZED status.
   */
  @ExceptionHandler(JwtException.class)
  public ResponseEntity<Map<String, Object>> handleJwtException(
      final JwtException ex
  ) {
    return buildErrorResponse(
        HttpStatus.UNAUTHORIZED,
        "Invalid Token",
        ex.getMessage());
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
    return buildErrorResponse(
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
    return buildErrorResponse(
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
    return buildErrorResponse(
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
    return buildErrorResponse(
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
    return buildErrorResponse(HttpStatus.UNAUTHORIZED,
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
  public ResponseEntity<Map<String, Object>> handleGeneric(
      final Exception ex
  ) {
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
        "Server Error",
        ex.getMessage());
  }
}

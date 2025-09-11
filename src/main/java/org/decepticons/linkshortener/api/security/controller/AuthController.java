package org.decepticons.linkshortener.api.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.decepticons.linkshortener.api.dto.AuthRequestDto;
import org.decepticons.linkshortener.api.dto.AuthResponseDto;
import org.decepticons.linkshortener.api.dto.RegistrationRequestDto;
import org.decepticons.linkshortener.api.security.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user authentication.
 * Provides endpoints for registration, login, and token refresh.
 */
@Tag(name = "Authentication", description = "User authentication and management")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  /** Service for authentication and user registration operations. */
  private final AuthService authService;

  /**
   * Registers a new user.
   *
   * @param request the registration request
   * @return the username of the newly created user
   */
  @PostMapping("/register")
  @Operation(summary = "Register a new user")
  public ResponseEntity<String> createUser(
      @Valid @RequestBody final RegistrationRequestDto request
  ) {
    String username = authService.registerUser(request);
    return ResponseEntity.ok(username);
  }

  /**
   * Authenticates a user and returns JWT token.
   *
   * @param request the login request
   * @return authentication response with JWT token
   */
  @PostMapping("/login")
  @Operation(summary = "Log in and get JWT tokens")
  public ResponseEntity<AuthResponseDto> authenticate(
      @RequestBody final AuthRequestDto request
  ) {
    AuthResponseDto response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  /**
   * Refreshes JWT token.
   *
   * @param authHeader the Authorization header containing Bearer token
   * @return authentication response with refreshed JWT token
   */
  @PostMapping("/refresh")
  @Operation(summary = "Refresh JWT access token")
  public ResponseEntity<AuthResponseDto> refreshToken(
      @RequestHeader("Authorization") final String authHeader
  ) {
    AuthResponseDto response = authService.refreshToken(authHeader);
    return ResponseEntity.ok(response);
  }
}

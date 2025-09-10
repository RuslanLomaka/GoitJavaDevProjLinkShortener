package org.decepticons.linkshortener.api.security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.decepticons.linkshortener.api.dto.AuthRequest;
import org.decepticons.linkshortener.api.dto.AuthResponse;
import org.decepticons.linkshortener.api.dto.RegistrationRequest;
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
  public ResponseEntity<String> createUser(
      @Valid @RequestBody final RegistrationRequest request
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
  public ResponseEntity<AuthResponse> authenticate(
      @RequestBody final AuthRequest request
  ) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  /**
   * Refreshes JWT token.
   *
   * @param authHeader the Authorization header containing Bearer token
   * @return authentication response with refreshed JWT token
   */
  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refreshToken(
      @RequestHeader("Authorization") final String authHeader
  ) {
    AuthResponse response = authService.refreshToken(authHeader);
    return ResponseEntity.ok(response);
  }
}

package org.decepticons.linkshortener.api.v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.decepticons.linkshortener.api.dto.AuthRequestDto;
import org.decepticons.linkshortener.api.dto.AuthResponseDto;
import org.decepticons.linkshortener.api.dto.RegistrationRequestDto;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.security.jwt.JwtTokenUtil;
import org.decepticons.linkshortener.api.security.model.CustomUserDetails;
import org.decepticons.linkshortener.api.security.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user authentication.
 * Provides endpoints for user registration, login, and token refresh.
 */
@Tag(
    name = "Authentication",
    description = "User authentication and management"
)
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  /** Service for authentication and user registration operations. */
  private final AuthService authService;

  /** Utility for generating and validating JWT tokens. */
  private final JwtTokenUtil jwtUtil;

  /**
   * Registers a new user.
   *
   * @param request the registration request DTO
   * @return the username of the newly created user
   */
  @PostMapping("/register")
  @Operation(summary = "Register a new user")
  public ResponseEntity<String> createUser(
      @Valid @RequestBody final RegistrationRequestDto request
  ) {
    User user = new User();
    user.setUsername(request.getUsername());
    user.setPasswordHash(request.getPassword());

    User registeredUser = authService.registerUser(user);
    return ResponseEntity.ok(registeredUser.getUsername());
  }

  /**
   * Authenticates a user and returns JWT tokens.
   *
   * @param request the login request DTO
   * @return authentication response with JWT tokens
   */
  @PostMapping("/login")
  @Operation(summary = "Log in and get JWT tokens")
  public ResponseEntity<AuthResponseDto> authenticate(
      @RequestBody final AuthRequestDto request
  ) {
    User user = authService.login(request.getUsername(), request.getPassword());
    UserDetails userDetails = new CustomUserDetails(user);
    String accessToken = jwtUtil.generateAccessToken(userDetails);
    String refreshToken = jwtUtil.generateRefreshToken(userDetails);
    List<String> roles = userDetails.getAuthorities().stream()
        .map(Object::toString)
        .collect(Collectors.toList());

    return ResponseEntity.ok(new AuthResponseDto(
        user.getUsername(),
        roles,
        accessToken,
        refreshToken
    ));
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
    User user = authService.refreshToken(authHeader);
    UserDetails userDetails = new CustomUserDetails(user);
    String newAccessToken = jwtUtil.generateAccessToken(userDetails);
    String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
    List<String> roles = userDetails.getAuthorities().stream()
        .map(Object::toString)
        .collect(Collectors.toList());

    return ResponseEntity.ok(new AuthResponseDto(
        user.getUsername(),
        roles,
        newAccessToken,
        newRefreshToken
    ));
  }
}

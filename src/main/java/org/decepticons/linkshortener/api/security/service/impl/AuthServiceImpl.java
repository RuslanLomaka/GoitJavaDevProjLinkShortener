package org.decepticons.linkshortener.api.security.service.impl;

import lombok.RequiredArgsConstructor;
import org.decepticons.linkshortener.api.dto.AuthRequest;
import org.decepticons.linkshortener.api.dto.AuthResponse;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.security.jwt.JwtTokenUtil;
import org.decepticons.linkshortener.api.security.model.CustomUserDetails;
import org.decepticons.linkshortener.api.security.service.AuthService;
import org.decepticons.linkshortener.api.security.service.UserAuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service implementation for user authentication,
 * token refresh, and registration.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  /** The prefix for the Authorization header. */
  private static final String BEARER_PREFIX = "Bearer ";

  /** Service for accessing and managing users. */
  private final UserAuthService userService;

  /** Spring Security authentication manager. */
  private final AuthenticationManager authManager;

  /** Utility for generating and validating JWT tokens. */
  private final JwtTokenUtil jwtUtil;

  /**
   * Authenticates a user and generates a JWT token.
   *
   * @param request the authentication request containing username and password
   * @return authentication response with JWT token
   */
  @Override
  public AuthResponse login(final AuthRequest request) {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.username(),
            request.password())
    );

    UserDetails details = (UserDetails) auth.getPrincipal();
    String token = jwtUtil.generateToken(details);

    return new AuthResponse(
        details.getUsername(),
        details.getAuthorities(),
        token);
  }

  /**
   * Refreshes a JWT token if valid.
   *
   * @param authorizationHeader the Authorization header containing Bearer token
   * @return authentication response with refreshed JWT token
   */
  @Override
  public AuthResponse refreshToken(final String authorizationHeader) {
    if (authorizationHeader == null
        || !authorizationHeader.startsWith(BEARER_PREFIX)) {
      throw new IllegalArgumentException("Invalid or missing token");
    }

    String jwtToken = authorizationHeader.substring(BEARER_PREFIX.length());
    String username = jwtUtil.extractUsername(jwtToken);

    User user = userService.findByUsername(username);
    UserDetails details = new CustomUserDetails(user);

    if (!jwtUtil.validateToken(jwtToken, details)) {
      throw new IllegalArgumentException("Token expired or invalid");
    }

    String refreshedToken = jwtUtil.refreshToken(jwtToken);
    return new AuthResponse(
        details.getUsername(),
        details.getAuthorities(),
        refreshedToken);
  }

  /**
   * Registers a new user.
   *
   * @param request the authentication request containing username and password
   * @return the username of the newly registered user
   */
  @Override
  public String registerUser(final AuthRequest request) {
    return userService.registerUser(request);
  }
}

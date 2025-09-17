package org.decepticons.linkshortener.api.security.service.impl;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.decepticons.linkshortener.api.exception.InvalidPasswordException;
import org.decepticons.linkshortener.api.exception.InvalidTokenException;
import org.decepticons.linkshortener.api.exception.UserAlreadyExistsException;
import org.decepticons.linkshortener.api.model.RevokedToken;
import org.decepticons.linkshortener.api.model.Role;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.model.UserStatus;
import org.decepticons.linkshortener.api.repository.RevokedTokenRepository;
import org.decepticons.linkshortener.api.repository.RoleRepository;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.decepticons.linkshortener.api.security.jwt.JwtTokenUtil;
import org.decepticons.linkshortener.api.security.service.AuthService;
import org.decepticons.linkshortener.api.service.UserService;
import org.decepticons.linkshortener.api.util.PasswordValidator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service implementation for user authentication and authorization.
 * Handles user login, token
 * refresh, and registration processes.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  /**
   * The prefix for a Bearer token in the Authorization header.
   */
  private static final String BEARER_PREFIX = "Bearer ";

  /**
   * Service for handling user-related authentication operations.
   */
  private final UserService userService;

  /**
   * Manages authentication requests and processes them.
   */
  private final AuthenticationManager authManager;

  /**
   * Utility for generating and validating JWT tokens.
   */
  private final JwtTokenUtil jwtUtil;

  /**
   * Repository for user data access.
   */
  private final UserRepository userRepository;

  /**
   * Repository for managing revoked JWT tokens.
   */
  private final RevokedTokenRepository revokedTokenRepository;

  /**
   * Encodes and verifies user passwords.
   */
  private final PasswordEncoder passwordEncoder;

  /**
   * Repository for role data access.
   */
  private final RoleRepository roleRepository;

  /**
   * Authenticates a user with the provided username and password.
   *
   * @param username The user's username.
   * @param password The user's password.
   * @return The authenticated User entity.
   */
  @Override
  public User login(final String username, final String password) {
    authManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password)
    );
    return userService.findByUsername(username);
  }

  /**
   * Refreshes a user's token based on the authorization header.
   *
   * @param authorizationHeader The header containing the refresh token.
   * @return The User associated with the refresh token.
   * @throws InvalidTokenException if the header is missing or malformed.
   */
  @Override
  public User refreshToken(final String authorizationHeader) {
    if (
        authorizationHeader == null
            || !authorizationHeader.startsWith(BEARER_PREFIX)
    ) {
      throw new InvalidTokenException(
          "Missing or malformed Authorization header"
      );
    }
    String jwtToken = authorizationHeader.substring(BEARER_PREFIX.length());

    if (revokedTokenRepository.existsByToken(jwtToken)) {
      throw new InvalidTokenException("Token has been revoked");
    }

    if (!jwtUtil.validateToken(jwtToken)) {
      throw new InvalidTokenException("Invalid or expired token");
    }

    String username = jwtUtil.extractUsername(jwtToken);
    return userService.findByUsername(username);
  }

  /**
   * Registers a new user with a default 'ROLE_USER' role.
   *
   * @param user The user entity to register.
   * @return The newly registered user.
   * @throws IllegalStateException if the default 'ROLE_USER' role is not found.
   */
  @Override
  @Transactional
  public User registerUser(final User user) {
    Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
    if (userRole.isEmpty()) {
      throw new IllegalStateException(
          "Default role 'ROLE_USER' not found in the database."
      );
    }

    if (userRepository.existsByUsername(user.getUsername())) {
      throw new UserAlreadyExistsException(user.getUsername());
    }

    if (!PasswordValidator.isValid(user.getPasswordHash())) {
      throw new InvalidPasswordException(
          "Password does not meet complexity requirements"
      );
    }

    user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
    user.setStatus(UserStatus.ACTIVE);
    user.setRoles(Collections.singleton(userRole.get()));
    return userRepository.save(user);
  }

  /**
   * Logs out the user by revoking their token.
   *
   * @param authHeader the Authorization header containing
   *                   the token to be revoked
   */
  @Override
  @Transactional
  public void logout(final String authHeader) {
    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      throw new InvalidTokenException(
          "Missing or malformed Authorization header"
      );
    }

    String accessToken = authHeader.substring(BEARER_PREFIX.length());
    Instant accessExpiresAt = jwtUtil.extractExpiration(accessToken)
        .toInstant();
    revokedTokenRepository.save(new RevokedToken(accessToken, accessExpiresAt));
  }
}

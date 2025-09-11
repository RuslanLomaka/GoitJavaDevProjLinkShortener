package org.decepticons.linkshortener.api.security.service;

import org.decepticons.linkshortener.api.dto.AuthRequestDto;
import org.decepticons.linkshortener.api.dto.AuthResponseDto;
import org.decepticons.linkshortener.api.dto.RegistrationRequestDto;


/**
 * Service interface for authentication and user registration.
 * Provides methods to login, refresh JWT tokens, and register new users.
 */
public interface AuthService {

  /**
   * Authenticates a user using the given login request and
   * returns an AuthResponse.
   *
   * @param loginRequest the login data (username and password)
   * @return an AuthResponse containing JWT token and user details
   */
  AuthResponseDto login(AuthRequestDto loginRequest);

  /**
   * Refreshes the JWT token if it is valid and not expired.
   *
   * @param token the JWT token to refresh
   * @return an AuthResponse with a new JWT token
   */
  AuthResponseDto refreshToken(String token);

  /**
   * Registers a new user based on the given registration request.
   *
   * @param request the registration data (username and password)
   * @return the username of the newly registered user
   */
  String registerUser(RegistrationRequestDto request);
}

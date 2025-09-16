package org.decepticons.linkshortener.api.security.service;

import org.decepticons.linkshortener.api.model.User;

/**
 * Service interface for authentication and user registration.
 * Provides methods to log in, refresh JWT tokens, and register new users.
 * The methods are designed to work with core domain objects to decouple
 * the business logic from the API layer's DTOs.
 */
public interface AuthService {

  /**
   * Authenticates a user using the provided username and password.
   *
   * @param username the username for login
   * @param password the password for login
   * @return the authenticated User domain object
   */
  User login(String username, String password);

  /**
   * Refreshes the JWT token if the provided token is valid.
   *
   * @param token the JWT token to refresh
   * @return the User domain object associated with the token
   */
  User refreshToken(String token);

  /**
   * Registers a new user based on the given domain User object.
   *
   * @param user the domain User object to register
   * @return the newly registered User domain object
   */
  User registerUser(User user);
}

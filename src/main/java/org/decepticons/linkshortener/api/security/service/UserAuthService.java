package org.decepticons.linkshortener.api.security.service;

import org.decepticons.linkshortener.api.dto.AuthRequest;
import org.decepticons.linkshortener.api.model.User;

/**
 * Service interface for user authentication and registration.
 */
public interface UserAuthService {

  /**
   * Finds a user by their username.
   *
   * @param username the username to search for
   * @return the User object
   */
  User findByUsername(String username);

  /**
   * Registers a new user with the provided authentication request.
   *
   * @param request the authentication request containing username and password
   * @return the username of the newly registered user
   */
  String registerUser(AuthRequest request);
}

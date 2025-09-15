package org.decepticons.linkshortener.api.security.service;

import org.decepticons.linkshortener.api.model.User;

/**
 * Service interface for user-related authentication and data retrieval.
 * This service works exclusively with the User domain object.
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
   * Registers a new user.
   *
   * @param user the User domain object to register
   * @return the registered User object
   */
  User registerUser(User user);
}

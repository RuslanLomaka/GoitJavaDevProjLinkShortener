package org.decepticons.linkshortener.api.service;

import java.util.UUID;
import org.decepticons.linkshortener.api.model.User;


/**
 * Service interface for retrieving information about the currently authenticated user.
 */
public interface UserService {

  /**
   * Retrieves the currently authenticated user.
   *
   * @return the User object representing the current user
   */
  User getCurrentUser();

  /**
   * Retrieves the unique identifier (UUID) of the currently authenticated user.
   *
   * @return the UUID of the current user
   */
  UUID getCurrentUserId();
}

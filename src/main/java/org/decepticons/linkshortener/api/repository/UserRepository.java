package org.decepticons.linkshortener.api.repository;

import java.util.Optional;
import org.decepticons.linkshortener.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link User} entities.
 * Provides CRUD operations and custom queries for retrieving users by username.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  /**
   * Finds a user by their unique username.
   *
   * @param username the username to search for
   * @return an {@link Optional} containing the {@link User} if found, or empty if not found
   */
  Optional<User> findByUsername(String username);

  /**
   * Checks whether a user with the specified username exists.
   *
   * @param username the username to check
   * @return {@code true} if a user with the username exists, {@code false} otherwise
   */
  boolean existsByUsername(String username);
}

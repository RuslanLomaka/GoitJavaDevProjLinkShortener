package org.decepticons.linkshortener.api.repository;

import java.util.Optional;
import org.decepticons.linkshortener.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



/**
 * Providing methods to interact with the database for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Finds a user by their username.
   *
   * @param username the username to search.
   * @return an optional containing the user if found
   */
  Optional<User> findByUsername(String username);


  /**
   * Verifies if a user exists by username.
   *
   * @param username the username to search.
   * @return true if the user exists, false otherwise
   */
  boolean existsByUsername(String username);
}

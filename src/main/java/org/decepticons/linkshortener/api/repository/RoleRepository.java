package org.decepticons.linkshortener.api.repository;

import java.util.Optional;
import org.decepticons.linkshortener.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Role} entities.
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {

  /**
   * Finds a Role by its name.
   *
   * @param name the name of the role to search for.
   * @return an {@link Optional} containing found Role, or empty if not found.
   */
  Optional<Role> findByName(String name);
}

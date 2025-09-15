package org.decepticons.linkshortener.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * Represents a user role in the system.
 * This class is used for Spring Security authorization.
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role implements GrantedAuthority {

  /**
   * The unique identifier for the role.
   */
  @Id
  private Integer id;

  /**
   * The name of the role (e.g., "ROLE_USER", "ROLE_ADMIN").
   */
  private String name;

  /**
   * A description of the role's purpose.
   */
  private String description;

  /**
   * Returns the authority string for this role.
   *
   * @return the name of the role.
   */
  @Override
  public String getAuthority() {
    return name;
  }
}

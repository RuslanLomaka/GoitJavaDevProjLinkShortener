package org.decepticons.linkshortener.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Domain entity representing an application user.
 * A {@code User} is the owner/creator of {@link Link} entities and is
 * associated with one or more shortened links. It stores identity and
 * status information used for authentication, authorization, and link
 * lifecycle management.
 * </p>
 *
 * <h2>Fields</h2>
 * <ul>
 *   <li>{@code id} – Surrogate primary key (UUID).</li>
 *   <li>{@code email} – Unique email address used as login identifier.</li>
 *   <li>{@code password} – Encrypted password for authentication.</li>
 *   <li>{@code status} – Current {@link UserStatus} (e.g. ACTIVE, BLOCKED).</li>
 *   <li>{@code createdAt} – Timestamp of initial registration.</li>
 *   <li>{@code updatedAt} – Timestamp of last update.</li>
 * </ul>
 *
 * @since 1.0
 * @author Ruslan Lomaka
 */

@Getter
@Entity
@Table(name = "users",
    uniqueConstraints = @UniqueConstraint(name = "uk_users_username", columnNames = "username"))
public class User {

  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "username", nullable = false, length = 64)
  private String username;

  @Setter
  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @Setter
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 16)
  private UserStatus status = UserStatus.ACTIVE;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User that)) {
      return false;
    }
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  /**
   * Sets the username for this user.
   * The username must be a non-null, non-blank value with a maximum length of 64 characters,
   * as enforced by the database column constraint.
   * </p>
   *
   * @param username the new username to assign to this user
   * @throws IllegalArgumentException if the username is null, blank, or exceeds 64 characters
   */
  public void setUsername(String username) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Username cannot be null or blank");
    }
    if (username.length() > 64) {
      throw new IllegalArgumentException("Username length must not exceed 64 characters");
    }
    this.username = username;
  }
}

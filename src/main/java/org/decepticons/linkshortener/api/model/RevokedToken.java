package org.decepticons.linkshortener.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a revoked JWT token in the database.
 * This is used for stateless token blacklisting.
 */
@Entity
@Table(name = "revoked_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RevokedToken {

  /**
   * The maximum length of a token string.
   */
  private static final int MAX_TOKEN_LENGTH = 500;

  /**
   * The unique identifier for the revoked token entry.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The revoked JWT token string.
   */
  @Column(nullable = false, unique = true, length = MAX_TOKEN_LENGTH)
  private String token;

  /**
   * The date and time at which the token expires.
   */
  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  /**
   * Constructs a new RevokedToken with the given token and expiration date.
   *
   * @param tokenParam the JWT token string to be revoked
   * @param expiresAtParam the expiration date of the token
   */
  public RevokedToken(final String tokenParam, final Instant expiresAtParam) {
    this.token = tokenParam;
    this.expiresAt = expiresAtParam;
  }
}

package org.decepticons.linkshortener.api.repository;

import java.time.Instant;
import java.util.Optional;
import org.decepticons.linkshortener.api.model.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing revoked JWT tokens.
 */
@Repository
public interface RevokedTokenRepository extends
    JpaRepository<RevokedToken, Long> {

  /**
   * Finds a revoked token by its unique token string.
   *
   * @param token The token string to search for.
   * @return An Optional containing the found RevokedToken,
   *                       or empty if not found.
   */
  Optional<RevokedToken> findByToken(String token);

  /**
   * Checks if a token exists in the repository.
   *
   * @param token The token string to check.
   * @return true if the token exists, false otherwise.
   */
  boolean existsByToken(String token);

  /**
   * Deletes all revoked tokens that have expired before the given timestamp.
   *
   * @param now The timestamp used to determine which tokens to delete.
   */
  void deleteAllByExpiresAtBefore(Instant now);
}

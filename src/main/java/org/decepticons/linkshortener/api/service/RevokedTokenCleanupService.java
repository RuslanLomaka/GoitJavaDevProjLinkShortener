package org.decepticons.linkshortener.api.service;

/**
 * Service interface for cleaning up revoked tokens.
 */
public interface RevokedTokenCleanupService {
  /**
   * Service interface for cleaning up revoked tokens.
   */
  void cleanupExpiredRevokedTokens();
}

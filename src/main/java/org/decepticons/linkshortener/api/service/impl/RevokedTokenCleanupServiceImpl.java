package org.decepticons.linkshortener.api.service.impl;

import java.time.Instant;
import org.decepticons.linkshortener.api.repository.RevokedTokenRepository;
import org.decepticons.linkshortener.api.service.RevokedTokenCleanupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service implementation for cleaning up expired revoked tokens.
 */
@Service
public class RevokedTokenCleanupServiceImpl implements
    RevokedTokenCleanupService {

  /**
   * The repository for managing revoked tokens.
   */
  private final RevokedTokenRepository revokedTokenRepository;

  /**
   * Constructs a new RevokedTokenCleanupServiceImpl.
   *
   * @param revokedTokenRepositoryParam The repository for revoked tokens.
   */
  public RevokedTokenCleanupServiceImpl(
      final RevokedTokenRepository revokedTokenRepositoryParam
  ) {
    this.revokedTokenRepository = revokedTokenRepositoryParam;
  }

  /**
   * Deletes all expired revoked tokens. This job runs every hour.
   */
  @Override
  @Scheduled(cron = "0 0 * * * ?") // every hour
  public void cleanupExpiredRevokedTokens() {
    revokedTokenRepository.deleteAllByExpiresAtBefore(Instant.now());
  }
}

package org.decepticons.linkshortener.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import org.decepticons.linkshortener.api.repository.RevokedTokenRepository;
import org.decepticons.linkshortener.api.service.impl.RevokedTokenCleanupServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("RevokedTokenCleanupService Unit Tests")
class RevokedTokenCleanupServiceImplTest {

  @Mock
  private RevokedTokenRepository revokedTokenRepository;

  @InjectMocks
  private RevokedTokenCleanupServiceImpl revokedTokenCleanupService;

  @Test
  @DisplayName("should call repository to delete expired tokens")
  void shouldCallRepositoryToDeleteExpiredTokens() {
    // When the scheduled cleanup method is called
    revokedTokenCleanupService.cleanupExpiredRevokedTokens();

    // Then the deleteAllByExpiresAtBefore method should be called on the repository
    verify(revokedTokenRepository).deleteAllByExpiresAtBefore(any(Instant.class));
  }
}
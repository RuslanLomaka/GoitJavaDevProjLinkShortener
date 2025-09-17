package org.decepticons.linkshortener.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RevokedToken Model Tests")
class RevokedTokenTest {

  @Test
  @DisplayName("should create RevokedToken with all-args constructor")
  void shouldCreateRevokedTokenWithAllArgsConstructor() {
    // Given
    String tokenValue = "test.token.123";
    Instant expirationTime = Instant.now().plusSeconds(3600);

    // When
    RevokedToken revokedToken = new RevokedToken(tokenValue, expirationTime);

    // Then
    assertNotNull(revokedToken);
    assertEquals(tokenValue, revokedToken.getToken());
    assertEquals(expirationTime, revokedToken.getExpiresAt());
  }

  @Test
  @DisplayName("should set and get values correctly with setters and getters")
  void shouldSetAndGetValuesCorrectly() {
    // Given
    RevokedToken revokedToken = new RevokedToken();
    String newTokenValue = "new.test.token.456";
    Instant newExpirationTime = Instant.now().plusSeconds(7200);

    // When
    revokedToken.setToken(newTokenValue);
    revokedToken.setExpiresAt(newExpirationTime);

    // Then
    assertEquals(newTokenValue, revokedToken.getToken());
    assertEquals(newExpirationTime, revokedToken.getExpiresAt());
  }
}
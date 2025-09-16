package org.decepticons.linkshortener.api.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;



/**
 * Unit tests for the JwtTokenUtil class.
 * These tests ensure the correct behavior of token creation, validation, and claim extraction.
 */
@DisplayName("JWT Token Utility Unit Tests")
class JwtTokenUtilTest {

  private JwtTokenUtil jwtTokenUtil;
  private final String secretKey = "thisisaverylongandsecurekeyforjwttokenstesting1234567890";
  private final long expirationSeconds = 3600; // 1 hour
  private final long refreshExpirationSeconds = 604800; // 1 week

  @BeforeEach
  void setUp() {
    jwtTokenUtil = new JwtTokenUtil(secretKey, expirationSeconds, refreshExpirationSeconds);
  }

  @Test
  @DisplayName("given user details, when generating access token, then returns valid token")
  void givenUserDetails_whenGeneratingAccessToken_thenReturnValidToken() {
    // Given
    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("testuser");

    // When
    String actualToken = jwtTokenUtil.generateAccessToken(userDetails);

    // Then
    assertNotNull(actualToken);
    assertFalse(actualToken.isEmpty());
    assertEquals("testuser", jwtTokenUtil.extractUsername(actualToken));
    assertTrue(jwtTokenUtil.validateToken(actualToken, userDetails));
  }

  @Test
  @DisplayName("given user details, when generating refresh token, then returns valid token")
  void givenUserDetails_whenGeneratingRefreshToken_thenReturnValidToken() {
    // Given
    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("testuser");

    // When
    String actualToken = jwtTokenUtil.generateRefreshToken(userDetails);

    // Then
    assertNotNull(actualToken);
    assertFalse(actualToken.isEmpty());
    assertEquals("testuser", jwtTokenUtil.extractUsername(actualToken));
  }

  @Test
  @DisplayName("given a valid token, when extracting username, then returns correct username")
  void givenValidToken_whenExtractingUsername_thenReturnCorrectUsername() {
    // Given
    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("testuser");
    String testToken = jwtTokenUtil.generateAccessToken(userDetails);

    // When
    String actualUsername = jwtTokenUtil.extractUsername(testToken);

    // Then
    String expectedUsername = "testuser";
    assertEquals(expectedUsername, actualUsername);
  }

  @Test
  @DisplayName("given a valid and unexpired token, when validating, then returns true")
  void givenValidToken_whenValidating_thenReturnTrue() {
    // Given
    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("testuser");
    String testToken = jwtTokenUtil.generateAccessToken(userDetails);

    // When
    boolean isValid = jwtTokenUtil.validateToken(testToken, userDetails);

    // Then
    assertTrue(isValid);
  }

  @Test
  @DisplayName("given an invalid token, when validating, then returns false")
  void givenInvalidToken_whenValidating_thenReturnFalse() {
    // Given
    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("testuser");
    String tamperedToken = "header.eyJzdWIiOiJ0YW1wZXJlZHVzZXIifQ.tampered_signature";

    // When
    boolean isValid = jwtTokenUtil.validateToken(tamperedToken, userDetails);

    // Then
    assertFalse(isValid);
  }

  @Test
  @DisplayName("given an expired token, when validating, then returns false")
  void givenExpiredToken_whenValidating_thenReturnFalse() {
    // Given
    long pastExpirationTime = System.currentTimeMillis() - 1000;
    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
    String expiredToken = Jwts.builder()
        .setSubject("expireduser")
        .setIssuedAt(new Date(pastExpirationTime - 1000))
        .setExpiration(new Date(pastExpirationTime))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("expireduser");

    // When
    boolean isValid = jwtTokenUtil.validateToken(expiredToken, userDetails);

    // Then
    assertFalse(isValid);
  }
}
package org.decepticons.linkshortener.api.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


/**
 * Utility class for generating and validating JWT tokens.
 * Provides methods to create JWT tokens for authenticated users,
 * refresh tokens, extract claims, and validate expiration.
 */
@Component
public class JwtTokenUtil {

  /** The number of milliseconds in a second. */
  private static final long MILLISECONDS_IN_A_SECOND = 1000L;

  /** The signing key. */
  private final Key signingKey;

  /** The token validity duration for access tokens in seconds. */
  private final long expirationSeconds;

  /** The token validity duration for refresh tokens in seconds. */
  private final long refreshExpirationSeconds;

  /**
   * Constructs a JwtTokenUtil with the given secrets and expiration times.
   *
   * @param secretValue            the secret key for signing JWT tokens
   * @param expirationSecondsValue the access token validity duration in seconds
   * @param refreshExpirationValue the refresh token validity
   *                               duration in seconds
   */
  public JwtTokenUtil(
      @Value("${JWT_SECRET}") final String secretValue,
      @Value("3600") final long expirationSecondsValue,
      @Value("604800") final long refreshExpirationValue) {
    this.expirationSeconds = expirationSecondsValue;
    this.refreshExpirationSeconds = refreshExpirationValue;
    this.signingKey = getSignInKey(secretValue);
  }

  /**
   * Generates a standard access token for the given user.
   *
   * @param userDetails the user details
   * @return a signed JWT access token
   */
  public String generateAccessToken(final UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userDetails.getUsername(), expirationSeconds);
  }

  /**
   * Generates a refresh token with a longer expiration.
   *
   * @param userDetails the user details to include in the token
   * @return the generated JWT refresh token
   */
  public String generateRefreshToken(final UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(
        claims,
        userDetails.getUsername(),
        refreshExpirationSeconds);
  }

  /**
   * Validates a JWT token against user details and expiration.
   *
   * @param token the JWT token to validate.
   * @param userDetails the user details to validate against.
   * @return true if the token is valid, false otherwise.
   */
  public boolean validateToken(
      final String token,
      final UserDetails userDetails) {
    try {
      final String username = extractUsername(token);
      return (username.equals(userDetails.getUsername())
          && !isTokenExpired(token));
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Extracts the username (subject) from a JWT token.
   *
   * @param token the JWT token from which to extract the username.
   * @return the username from the token.
   */
  public String extractUsername(final String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts a specific claim from a JWT token.
   *
   * @param <T> the type of the claim to extract
   * @param token the JWT token from which to extract the claim
   * @param claimsResolver a function to resolve a specific claim
   *     from the token's claims
   * @return the extracted claim
   */
  public <T> T extractClaim(
      final String token,
      final Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Extracts the expiration date from a JWT token.
   *
   * @param token the JWT token from which to extract the expiration date.
   * @return the expiration date from the token.
   */
  public Date extractExpiration(final String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Builds a JWT token with claims and subject.
   *
   * @param claims the claims to be included in the token.
   * @param subject the subject of the token (usually the username).
   * @param expirationSecondsParam the token validity in seconds.
   * @return the built JWT token string.
   */
  private String createToken(
      final Map<String, Object> claims,
      final String subject,
      final long expirationSecondsParam
  ) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis()
            + expirationSecondsParam * MILLISECONDS_IN_A_SECOND))
        .signWith(signingKey, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Extracts all claims from a JWT token.
   *
   * @param token the JWT token
   * @return the claims
   */

  private Claims extractAllClaims(final String token) {
    return Jwts.parserBuilder()
        .setSigningKey(signingKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * Checks if a token is expired.
   *
   * @param token the JWT token
   * @return true if expired, false otherwise
   */

  private boolean isTokenExpired(final String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Derives a signing key from the secret.
   *
   * @param secret the secret key as a string
   * @return the signing key
   */

  private Key getSignInKey(final String secret) {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}

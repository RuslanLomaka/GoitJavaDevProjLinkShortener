package org.decepticons.linkshortener.api.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.decepticons.linkshortener.api.exceptions.ExpiredTokenException;
import org.decepticons.linkshortener.api.exceptions.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Utility class for generating, validating, and parsing JWT tokens.
 * Provides methods to create JWT tokens for authenticated users,
 * refresh tokens, extract claims, and validate expiration.
 */
@Component
public class JwtTokenUtil {

  /** Token validity duration in seconds. */
  private final long expirationSeconds;

  /** The signing key derived from the secret. */
  private final Key signingKey;

  /** Constant for converting seconds to milliseconds. */
  private static final long MILLISECONDS_IN_SECOND = 1000L;

  /**
   * Constructs a JwtTokenUtil with the given secret and token expiration time.
   *
   * @param secretValue            the secret key used for signing JWT tokens
   * @param expirationSecondsValue the token validity duration in seconds
   */
  public JwtTokenUtil(@Value("${JWT_SECRET}") final String secretValue,
      @Value("${JWT_TTL_SECONDS}") final long expirationSecondsValue) {
    this.expirationSeconds = expirationSecondsValue;
    this.signingKey = getSignInKey(secretValue);
  }

  /**
   * Generates a JWT token for the given user.
   *
   * @param userDetails the user details
   * @return a signed JWT token
   */
  public String generateToken(final UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userDetails.getUsername());
  }

  /**
   * Refreshes a JWT token by updating its issued and expiration times.
   *
   * @param token the existing JWT token to refresh
   * @return a refreshed JWT token
   */
  public String refreshToken(final String token) {
    Claims oldClaims = extractAllClaims(token);
    return createToken(oldClaims, oldClaims.getSubject());
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
      final UserDetails userDetails
  ) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())
        && !isTokenExpired(token));
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
   * @param <T>            the type of the claim to extract
   * @param token          the JWT token from which to extract the claim
   * @param claimsResolver a function to resolve a specific claim
   *                       from the token's claims
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
   * @return the built JWT token string.
   */
  private String createToken(
      final Map<String, Object> claims,
      final String subject
  ) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(
            new Date(System.currentTimeMillis()
                + expirationSeconds * MILLISECONDS_IN_SECOND)
        )
        .signWith(signingKey, SignatureAlgorithm.HS256)
        .compact();
  }

  private Claims extractAllClaims(final String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(signingKey)
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
      throw new ExpiredTokenException("JWT expired", e);
    } catch (io.jsonwebtoken.JwtException e) {
      throw new InvalidTokenException("Invalid JWT token", e);
    }
  }

  private boolean isTokenExpired(final String token) {
    return extractExpiration(token).before(new Date());
  }

  private Key getSignInKey(final String secret) {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}

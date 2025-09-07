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

  /**
   * The secret key used for signing JWT tokens.
   */
  private final String secret;

  /**
   * The token validity duration in seconds.
   */
  private final long expirationSeconds;

  /**
   * The signing key derived from the secret.
   */
  private final Key signingKey;

  /**
   * Constructs a JwtTokenUtil with the given secret and token expiration time.
   *
   * @param secretValue            the secret key used for signing JWT tokens
   * @param expirationSecondsValue the token validity duration in seconds
   */

  public JwtTokenUtil(@Value("${JWT_SECRET}") final String secretValue,
      @Value("${JWT_TTL_SECONDS}") final long expirationSecondsValue) {
    this.secret = secretValue;
    this.expirationSeconds = expirationSecondsValue;
    this.signingKey = getSignInKey();
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
   * Constant for converting seconds to milliseconds.
   */
  private static final long MILLISECONDS_IN_SECOND = 1000L;

  /**
   * Refreshes a JWT token by updating its issued and expiration times.
   *
   * @param token the existing JWT token
   * @return a refreshed JWT token
   */

  public String refreshToken(final String token) {
    final Claims claims = extractAllClaims(token);
    claims.setIssuedAt(new Date(System.currentTimeMillis()));
    claims.setExpiration(new Date(System.currentTimeMillis()
        + expirationSeconds * MILLISECONDS_IN_SECOND));
    return Jwts.builder()
        .setClaims(claims)
        .signWith(signingKey, SignatureAlgorithm.HS512)
        .compact();
  }

  /**
   * Validates a JWT token against user details and expiration.
   *
   * @param token       the JWT token
   * @param userDetails the user details
   * @return true if valid, false otherwise
   */

  public boolean validateToken(final String token,
      final UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())
        && !isTokenExpired(token));
  }

  /**
   * Extracts the username (subject) from a JWT token.
   *
   * @param token The JWT token.
   * @return The username contained in the token.
   */

  public String extractUsername(final String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts a specific claim from a JWT
   * token using a claims resolver function.
   *
   * @param <T> the type of the claim
   * @param token the JWT token
   * @param claimsResolver Function to extract the claim from Claims.
   * @return the extracted claim
   */

  public <T> T extractClaim(final String token,
      final Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Extracts the expiration date from a JWT token.
   *
   * @param token the JWT token
   * @return the expiration date
   */

  public Date extractExpiration(final String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private String createToken(final Map<String, Object> claims,
      final String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis()
            + expirationSeconds * MILLISECONDS_IN_SECOND))
        .signWith(signingKey, SignatureAlgorithm.HS512)
        .compact();
  }

  private Claims extractAllClaims(final String token) {
    return Jwts.parserBuilder()
        .setSigningKey(signingKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private boolean isTokenExpired(final String token) {
    return extractExpiration(token).before(new Date());
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}

package org.decepticons.linkshortener.api.dto;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

/**
 * Represents an authentication response returned to the client
 * after successful login.
 */
@Getter
@Setter
public class AuthResponse {

  /**
   * The username of the authenticated user.
   */
  private String username;

  /**
   * Authorities granted to the authenticated user.
   */
  private Collection<? extends GrantedAuthority> authorities;

  /**
   * JWT token for authentication.
   */
  private String token;

  /**
   * Creates a new AuthResponse.
   *
   * @param usernameParam    the username of the authenticated user
   * @param authoritiesParam the authorities granted to the user
   * @param tokenParam       the JWT token
   */
  public AuthResponse(final String usernameParam,
      final Collection<? extends GrantedAuthority> authoritiesParam,
      final String tokenParam) {
    this.username = usernameParam;
    this.authorities = authoritiesParam;
    this.token = tokenParam;
  }
}

package org.decepticons.linkshortener.api.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

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
   * Roles (authorities) granted to the authenticated user.
   */
  private List<String> roles;

  /**
   * JWT token for authentication.
   */
  private String token;

  /**
   * Creates a new AuthResponse.
   *
   * @param usernameParam    the username of the authenticated user
   * @param rolesParam       the authorities granted to the user
   * @param tokenParam       the JWT token
   */
  public AuthResponse(final String usernameParam,
      final List<String> rolesParam,
      final String tokenParam) {
    this.username = usernameParam;
    this.roles = rolesParam;
    this.token = tokenParam;
  }
}

package org.decepticons.linkshortener.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Represents an authentication response returned to the client
 * after successful login.
 */
@Getter
@Setter
@NoArgsConstructor
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
   * JWT access token for API authentication.
   */
  private String accessToken;

  /**
   * JWT refresh token for obtaining a new access token.
   */
  private String refreshToken;

  /**
   * Creates a new AuthResponse.
   *
   * @param usernameParam    the username of the authenticated user
   * @param rolesParam       the authorities granted to the user
   * @param accessTokenParam       the JWT access token
   * @param refreshTokenParam      the JWT refresh token
   */
  public AuthResponse(final String usernameParam,
      final List<String> rolesParam,
      final String accessTokenParam,
      final String refreshTokenParam) {
    this.username = usernameParam;
    this.roles = rolesParam;
    this.accessToken = accessTokenParam;
    this.refreshToken = refreshTokenParam;
  }
}

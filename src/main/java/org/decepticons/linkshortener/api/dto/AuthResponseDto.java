package org.decepticons.linkshortener.api.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an authentication response returned to the client
 * after successful login.
 */
@Getter
@Setter
@NoArgsConstructor
public class AuthResponseDto {

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
   * Creates a new AuthResponseDto.
   *
   * @param usernameParam    the username of the authenticated user
   * @param rolesParam       the authorities granted to the user
   * @param accessTokenParam       the JWT access token
   * @param refreshTokenParam      the JWT refresh token
   */
  public AuthResponseDto(final String usernameParam,
      final List<String> rolesParam,
      final String accessTokenParam,
      final String refreshTokenParam) {
    this.username = usernameParam;
    this.roles = rolesParam;
    this.accessToken = accessTokenParam;
    this.refreshToken = refreshTokenParam;
  }
}

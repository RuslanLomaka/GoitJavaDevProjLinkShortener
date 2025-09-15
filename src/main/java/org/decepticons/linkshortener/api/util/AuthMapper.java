package org.decepticons.linkshortener.api.util;

import java.util.List;
import org.decepticons.linkshortener.api.dto.AuthResponseDto;
import org.decepticons.linkshortener.api.dto.RegistrationRequestDto;
import org.decepticons.linkshortener.api.model.User;
import org.springframework.security.core.GrantedAuthority;

/**
 * A utility class for mapping between authentication-related DTOs and entities.
 * This class contains static methods to convert data transfer objects
 * into domain entities and vice versa.
 */
public final class AuthMapper {

  private AuthMapper() {
    // no-op
  }

  /**
   * Converts a {@link RegistrationRequestDto} to a {@link User} entity.
   *
   * @param requestDto The DTO containing registration details.
   * @return A new User entity.
   */
  public static User toUserEntity(final RegistrationRequestDto requestDto) {
    User user = new User();
    user.setUsername(requestDto.getUsername());
    user.setPasswordHash(requestDto.getPassword());
    return user;
  }

  /**
   * Converts a {@link User} entity and authentication details
   * to an {@link AuthResponseDto}.
   *
   * @param user The authenticated user entity.
   * @param authorities A list of the user's granted authorities.
   * @param accessToken The JWT access token.
   * @param refreshToken The JWT refresh token.
   * @return An AuthResponseDto containing the user's details and tokens.
   */
  public static AuthResponseDto toAuthResponseDto(
      final User user,
      final List<GrantedAuthority> authorities,
      final String accessToken,
      final String refreshToken) {
    return new AuthResponseDto(
        user.getUsername(),
        authorities.stream().map(GrantedAuthority::getAuthority).toList(),
        accessToken,
        refreshToken
    );
  }
}

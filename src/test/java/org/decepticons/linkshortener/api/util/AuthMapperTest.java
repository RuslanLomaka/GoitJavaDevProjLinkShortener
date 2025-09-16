package org.decepticons.linkshortener.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.List;
import org.decepticons.linkshortener.api.dto.AuthResponseDto;
import org.decepticons.linkshortener.api.dto.RegistrationRequestDto;
import org.decepticons.linkshortener.api.model.Role;
import org.decepticons.linkshortener.api.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;


@DisplayName("AuthMapper Unit Tests")
class AuthMapperTest {

  @Test
  @DisplayName("should map RegistrationRequestDto to User entity")
  void shouldMapRegistrationRequestDtoToUser() {
    // Given
    RegistrationRequestDto requestDto = new RegistrationRequestDto();
    requestDto.setUsername("testuser");
    requestDto.setPassword("password123");

    // When
    User user = AuthMapper.toUserEntity(requestDto);

    // Then
    assertNotNull(user);
    assertEquals("testuser", user.getUsername());
    assertEquals("password123", user.getPasswordHash());
  }

  @Test
  @DisplayName("should map User entity and tokens to AuthResponseDto")
  void shouldMapUserAndTokensToAuthResponseDto() {
    // Given
    User user = new User();
    user.setUsername("testuser");

    Role role = new Role(1, "ROLE_USER", "Standard user role");
    List<GrantedAuthority> authorities = Collections.singletonList(role);

    String accessToken = "mocked.access.token";
    String refreshToken = "mocked.refresh.token";

    // When
    AuthResponseDto responseDto
        = AuthMapper.toAuthResponseDto(user, authorities, accessToken, refreshToken);

    // Then
    assertNotNull(responseDto);
    assertEquals("testuser", responseDto.getUsername());
    assertEquals(accessToken, responseDto.getAccessToken());
    assertEquals(refreshToken, responseDto.getRefreshToken());
    assertFalse(responseDto.getRoles().isEmpty());
    assertEquals("ROLE_USER", responseDto.getRoles().get(0));
  }
}
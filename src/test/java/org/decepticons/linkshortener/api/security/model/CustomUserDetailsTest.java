package org.decepticons.linkshortener.api.security.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import org.decepticons.linkshortener.api.model.Role;
import org.decepticons.linkshortener.api.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CustomUserDetails Unit Tests")
class CustomUserDetailsTest {

  @Test
  @DisplayName("should create CustomUserDetails from a User object")
  void shouldCreateUserDetailsFromUser() {
    // Given
    User user = new User();
    user.setUsername("testuser");
    user.setPasswordHash("encoded_password");

    // When
    CustomUserDetails userDetails = new CustomUserDetails(user);

    // Then
    assertNotNull(userDetails);
    assertEquals("testuser", userDetails.getUsername());
    assertEquals("encoded_password", userDetails.getPassword());
    assertTrue(userDetails.getAuthorities().isEmpty());
  }

  @Test
  @DisplayName("should return correct authorities for a user with roles")
  void shouldReturnCorrectAuthorities() {
    // Given
    User user = new User();
    user.setUsername("adminuser");
    user.setPasswordHash("encoded_password");
    user.setRoles(Collections.singleton(new Role(1, "ROLE_ADMIN", "Admin role")));

    // When
    CustomUserDetails userDetails = new CustomUserDetails(user);

    // Then
    assertNotNull(userDetails.getAuthorities());
    assertFalse(userDetails.getAuthorities().isEmpty());
    assertEquals(1, userDetails.getAuthorities().size());
    assertEquals("ROLE_ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
  }

  @Test
  @DisplayName("should return true for account and credential status")
  void shouldReturnTrueForAccountStatus() {
    // Given
    User user = new User();
    user.setUsername("testuser");
    user.setPasswordHash("encoded_password");

    // When
    CustomUserDetails userDetails = new CustomUserDetails(user);

    // Then
    assertTrue(userDetails.isAccountNonExpired());
    assertTrue(userDetails.isAccountNonLocked());
    assertTrue(userDetails.isCredentialsNonExpired());
    assertTrue(userDetails.isEnabled());
  }
}
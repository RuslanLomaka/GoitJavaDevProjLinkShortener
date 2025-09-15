package org.decepticons.linkshortener.api.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Role Class Unit Tests")
class RoleTest {

  @Test
  @DisplayName("should create a role with all fields populated via constructor")
  void shouldCreateRoleWithAllFields() {
    // Given
    Integer id = 1;
    String name = "ROLE_USER";
    String description = "Standard user role";

    // When
    Role role = new Role(id, name, description);

    // Then
    assertNotNull(role);
    assertEquals(id, role.getId());
    assertEquals(name, role.getName());
    assertEquals(description, role.getDescription());
  }

  @Test
  @DisplayName("should return the role name as authority")
  void shouldReturnRoleNameAsAuthority() {
    // Given
    Role role = new Role(1, "ROLE_ADMIN", "Administrator role");

    // When
    String authority = role.getAuthority();

    // Then
    assertEquals("ROLE_ADMIN", authority);
  }
}
package org.decepticons.linkshortener.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.decepticons.linkshortener.api.exception.NoSuchUserFoundInTheSystemException;
import org.decepticons.linkshortener.api.exception.UserNotFoundException;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.decepticons.linkshortener.api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for the UserServiceImpl class.
 * These tests focus on the user retrieval logic using mocks for dependencies.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  @DisplayName("Test getCurrentUser returns correct User")
  void testGetCurrentUserSuccess() {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn("testuser");
    when(securityContext.getAuthentication()).thenReturn(authentication);
    org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);

    User fakeUser = new User();
    fakeUser.setUsername("testuser");

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));

    User result = userService.getCurrentUser();

    assertNotNull(result);
    assertEquals("testuser", result.getUsername());

    org.springframework.security.core.context.SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Test getCurrentUserId returns correct UUID")
  void testGetCurrentUserIdSuccess() {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn("testuser");
    when(securityContext.getAuthentication()).thenReturn(authentication);
    org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);

    UUID fakeId = UUID.randomUUID();
    User fakeUser = new User();
    ReflectionTestUtils.setField(fakeUser, "id", fakeId);
    fakeUser.setUsername("testuser");

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));

    UUID result = userService.getCurrentUserId();

    assertEquals(fakeId, result);

    org.springframework.security.core.context.SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Test getCurrentUser throws exception when user not found")
  void testGetCurrentUserUserNotFound() {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn("testuser");
    when(securityContext.getAuthentication()).thenReturn(authentication);
    org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

    NoSuchUserFoundInTheSystemException ex = assertThrows(
        NoSuchUserFoundInTheSystemException.class,
        () -> userService.getCurrentUser()
    );

    assertTrue(ex.getMessage().contains("No such user found in the system"));

    org.springframework.security.core.context.SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Test findByUsername returns a user when found")
  void testFindByUsernameSuccess() {
    // Given
    String username = "testuser";
    User expectedUser = new User();
    expectedUser.setUsername(username);
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

    // When
    User actualUser = userService.findByUsername(username);

    // Then
    assertNotNull(actualUser);
    assertEquals(username, actualUser.getUsername());
  }

  @Test
  @DisplayName("Test findByUsername throws an exception when user is not found")
  void testFindByUsernameNotFound() {
    // Given
    String username = "nonexistentuser";
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(UserNotFoundException.class, () -> userService.findByUsername(username));
  }

}

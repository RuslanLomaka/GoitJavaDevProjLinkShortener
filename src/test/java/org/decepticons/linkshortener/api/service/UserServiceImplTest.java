package org.decepticons.linkshortener.api.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.decepticons.linkshortener.api.exception.NoSuchUserFoundInTheSystemException;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.decepticons.linkshortener.api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  @BeforeEach
  void setUp() {
    when(authentication.getName()).thenReturn("testuser");
    when(securityContext.getAuthentication()).thenReturn(authentication);
    org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);
  }

  @AfterEach
  void tearDown() {
    org.springframework.security.core.context.SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Test getCurrentUser returns correct User")
  void testGetCurrentUserSuccess() {
    User fakeUser = new User();
    fakeUser.setUsername("testuser");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
    User result = userService.getCurrentUser();
    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
  }

  @Test
  @DisplayName("Test getCurrentUserId returns correct UUID")
  void testGetCurrentUserIdSuccess() {
    UUID fakeId = UUID.randomUUID();
    User fakeUser = new User();
    ReflectionTestUtils.setField(fakeUser, "id", fakeId);
    fakeUser.setUsername("testuser");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
    UUID result = userService.getCurrentUserId();
    assertEquals(fakeId, result);
  }

  @Test
  @DisplayName("Test getCurrentUser throws exception when user not found")
  void testGetCurrentUserUserNotFound() {
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
    NoSuchUserFoundInTheSystemException ex = assertThrows(
        NoSuchUserFoundInTheSystemException.class,
        () -> userService.getCurrentUser()
    );
    assertTrue(ex.getMessage().contains("No such user found in the system"));
  }

}

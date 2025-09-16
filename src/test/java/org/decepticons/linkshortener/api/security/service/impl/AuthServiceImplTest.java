package org.decepticons.linkshortener.api.security.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import org.decepticons.linkshortener.api.exception.InvalidTokenException;
import org.decepticons.linkshortener.api.exception.UserAlreadyExistsException;
import org.decepticons.linkshortener.api.model.Role;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.model.UserStatus;
import org.decepticons.linkshortener.api.repository.RoleRepository;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.decepticons.linkshortener.api.security.jwt.JwtTokenUtil;
import org.decepticons.linkshortener.api.security.service.UserAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;



/**
 * Unit tests for the AuthServiceImpl class.
 * These tests use Mockito to simulate dependencies and test the business logic in isolation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Unit Tests")
class AuthServiceImplTest {

  @Mock
  private UserAuthService userAuthService;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtTokenUtil jwtUtil;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private RoleRepository roleRepository;

  @InjectMocks
  private AuthServiceImpl authService;

  private User testUser;
  private UserDetails userDetails;
  private String expectedAccessToken;
  private String expectedRefreshToken;
  private Role userRole;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setUsername("testuser");
    testUser.setPasswordHash("encodedPassword123");

    userDetails = new org.springframework.security.core.userdetails.User(
        "testuser",
        "encodedPassword123",
        Collections.emptyList()
    );

    userRole = new Role(1, "ROLE_USER", "Standard user role");

    expectedAccessToken = "valid.access.token";
    expectedRefreshToken = "valid.refresh.token";
  }

  @Test
  @DisplayName("given valid credentials, when login, then returns a User object")
  void givenValidCredentials_whenLogin_thenReturnsUser() {
    // Given
    Authentication auth = mock(Authentication.class);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(auth);
    when(userAuthService.findByUsername("testuser")).thenReturn(testUser);

    // When
    User actualUser = authService.login("testuser", "password123");

    // Then
    assertNotNull(actualUser);
    assertEquals("testuser", actualUser.getUsername());
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userAuthService).findByUsername("testuser");
  }

  @Test
  @DisplayName("given invalid credentials, when login, then throws BadCredentialsException")
  void givenInvalidCredentials_whenLogin_thenThrowsBadCredentialsException() {
    // Given
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Invalid credentials"));

    // When & Then
    assertThrows(BadCredentialsException.class,
        () -> authService.login("testuser", "wrongpassword"));
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userAuthService, never()).findByUsername(anyString());
  }

  @Test
  @DisplayName("given a valid refresh token, when refreshing, then returns a User object")
  void givenValidRefreshToken_whenRefreshing_thenReturnsUser() {
    // Given
    String authHeader = "Bearer " + expectedRefreshToken;
    when(jwtUtil.extractUsername(expectedRefreshToken)).thenReturn("testuser");
    when(userAuthService.findByUsername("testuser")).thenReturn(testUser);

    // When
    User actualUser = authService.refreshToken(authHeader);

    // Then
    assertNotNull(actualUser);
    assertEquals("testuser", actualUser.getUsername());
    verify(jwtUtil).extractUsername(expectedRefreshToken);
    verify(userAuthService).findByUsername("testuser");
  }

  @Test
  @DisplayName("given a null or malformed header, "
      + "when refreshing, then throws InvalidTokenException")
  void givenMalformedHeader_whenRefreshing_thenThrowsInvalidTokenException() {
    // When & Then
    assertThrows(InvalidTokenException.class, () -> authService.refreshToken(null));
    assertThrows(InvalidTokenException.class, () -> authService.refreshToken("InvalidToken"));
  }

  @Test
  @DisplayName("given an existing user, when registering, then throws UserAlreadyExistsException")
  void givenExistingUser_whenRegistering_thenThrowsUserAlreadyExistsException() {
    // Given
    when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
    when(userAuthService.registerUser(any(User.class)))
        .thenThrow(new UserAlreadyExistsException("testuser"));

    // When & Then
    assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(testUser));
  }

  @Test
  @DisplayName("given a new user, when registering, then returns the created User object")
  void givenNewUser_whenRegistering_thenReturnsUser() {
    // Given
    when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
    // The stubbing is needed to ensure that userAuthService returns the mocked user object.
    when(userAuthService.registerUser(any(User.class))).thenReturn(testUser);

    // When
    User registeredUser = authService.registerUser(testUser);

    // Then
    assertNotNull(registeredUser);
    assertEquals("testuser", registeredUser.getUsername());
    assertEquals(UserStatus.ACTIVE, registeredUser.getStatus());
    assertFalse(registeredUser.getRoles().isEmpty());
    verify(userAuthService).registerUser(any(User.class));
  }
}
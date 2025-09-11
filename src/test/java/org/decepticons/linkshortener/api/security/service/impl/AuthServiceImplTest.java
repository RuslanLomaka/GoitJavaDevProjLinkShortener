package org.decepticons.linkshortener.api.security.service.impl;

import org.decepticons.linkshortener.api.dto.AuthRequestDto;
import org.decepticons.linkshortener.api.dto.AuthResponseDto;
import org.decepticons.linkshortener.api.dto.RegistrationRequestDto;
import org.decepticons.linkshortener.api.exceptions.ExpiredTokenException;
import org.decepticons.linkshortener.api.exceptions.InvalidTokenException;
import org.decepticons.linkshortener.api.exceptions.UserAlreadyExistsException;
import org.decepticons.linkshortener.api.exceptions.UserNotFoundException;
import org.decepticons.linkshortener.api.model.User;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AuthServiceImpl class.
 * These tests use Mockito to simulate dependencies and test the business logic in isolation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Unit Tests")
class AuthServiceImplTest {

  @Mock
  private UserAuthService userService;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtTokenUtil jwtUtil;

  @InjectMocks
  private AuthServiceImpl authService;

  private AuthRequestDto authRequestDto;
  private RegistrationRequestDto registrationRequestDto;
  private UserDetails userDetails;
  private String expectedAccessToken;
  private String expectedRefreshToken;

  @BeforeEach
  void setUp() {
    authRequestDto = new AuthRequestDto();
    authRequestDto.setUsername("testuser");
    authRequestDto.setPassword("password123");

    registrationRequestDto = new RegistrationRequestDto();
    registrationRequestDto.setUsername("newuser");
    registrationRequestDto.setPassword("Password123");

    userDetails = org.springframework.security.core.userdetails.User.builder()
        .username("testuser")
        .password("password123")
        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        .build();

    expectedAccessToken = "valid.access.token";
    expectedRefreshToken = "valid.refresh.token";
  }

  @Test
  @DisplayName("given valid credentials, when login, then returns tokens")
  void givenValidCredentials_whenLogin_thenReturnsTokens() {
    // Given
    Authentication auth = mock(Authentication.class);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(auth);
    when(auth.getPrincipal()).thenReturn(userDetails);
    when(jwtUtil.generateAccessToken(any(UserDetails.class))).thenReturn(expectedAccessToken);
    when(jwtUtil.generateRefreshToken(any(UserDetails.class))).thenReturn(expectedRefreshToken);

    // When
    AuthResponseDto actualResponse = authService.login(authRequestDto);

    // Then
    assertNotNull(actualResponse);
    assertEquals("testuser", actualResponse.getUsername());
    assertEquals(expectedAccessToken, actualResponse.getAccessToken());
    assertEquals(expectedRefreshToken, actualResponse.getRefreshToken());
    assertEquals(1, actualResponse.getRoles().size());
    assertEquals("ROLE_USER", actualResponse.getRoles().get(0));
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtUtil).generateAccessToken(any(UserDetails.class));
    verify(jwtUtil).generateRefreshToken(any(UserDetails.class));
  }

  @Test
  @DisplayName("given invalid credentials, when login, then throws BadCredentialsException")
  void givenInvalidCredentials_whenLogin_thenThrowsBadCredentialsException() {
    // Given
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Invalid credentials"));

    // When & Then
    assertThrows(BadCredentialsException.class, () -> authService.login(authRequestDto));
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtUtil, never()).generateAccessToken(any());
    verify(jwtUtil, never()).generateRefreshToken(any());
  }

  @Test
  @DisplayName("given a valid refresh token, when refreshing, then returns new tokens")
  void givenValidRefreshToken_whenRefreshing_thenReturnsNewTokens() {
    // Given
    String authHeader = "Bearer " + expectedRefreshToken;
    String expectedNewAccessToken = "new.access.token";
    String expectedNewRefreshToken = "new.refresh.token";
    User user = new User();
    user.setUsername("testuser");

    when(jwtUtil.extractUsername(expectedRefreshToken)).thenReturn("testuser");
    when(userService.findByUsername("testuser")).thenReturn(user);
    when(jwtUtil.validateToken(eq(expectedRefreshToken), any(UserDetails.class))).thenReturn(true);
    when(jwtUtil.generateAccessToken(any(UserDetails.class))).thenReturn(expectedNewAccessToken);
    when(jwtUtil.generateRefreshToken(any(UserDetails.class))).thenReturn(expectedNewRefreshToken);

    // When
    AuthResponseDto actualResponse = authService.refreshToken(authHeader);

    // Then
    assertNotNull(actualResponse);
    assertEquals(expectedNewAccessToken, actualResponse.getAccessToken());
    assertEquals(expectedNewRefreshToken, actualResponse.getRefreshToken());
    verify(jwtUtil).extractUsername(expectedRefreshToken);
    verify(userService).findByUsername("testuser");
    verify(jwtUtil).validateToken(any(), any());
    verify(jwtUtil).generateAccessToken(any());
    verify(jwtUtil).generateRefreshToken(any());
  }

  @Test
  @DisplayName("given a null or malformed header, when refreshing, then throws InvalidTokenException")
  void givenMalformedHeader_whenRefreshing_thenThrowsInvalidTokenException() {
    // When & Then
    assertThrows(InvalidTokenException.class, () -> authService.refreshToken(null));
    assertThrows(InvalidTokenException.class, () -> authService.refreshToken("InvalidToken"));
  }

  @Test
  @DisplayName("given an expired token, when refreshing, then throws ExpiredTokenException")
  void givenExpiredToken_whenRefreshing_thenThrowsExpiredTokenException() {
    // Given
    String authHeader = "Bearer " + expectedRefreshToken;
    User user = new User();
    user.setUsername("testuser");

    when(jwtUtil.extractUsername(expectedRefreshToken)).thenReturn("testuser");
    when(userService.findByUsername("testuser")).thenReturn(user);
    when(jwtUtil.validateToken(any(), any())).thenReturn(false);

    // When & Then
    assertThrows(ExpiredTokenException.class, () -> authService.refreshToken(authHeader));
  }

  @Test
  @DisplayName("given a valid token but user not found, when refreshing, then throws UserNotFoundException")
  void givenTokenForNonexistentUser_whenRefreshing_thenThrowsUserNotFoundException() {
    // Given
    String authHeader = "Bearer " + expectedRefreshToken;
    String nonexistentUsername = "nonexistentuser";

    when(jwtUtil.extractUsername(expectedRefreshToken)).thenReturn(nonexistentUsername);
    when(userService.findByUsername(nonexistentUsername)).thenThrow(new UserNotFoundException(nonexistentUsername));

    // When & Then
    assertThrows(UserNotFoundException.class, () -> authService.refreshToken(authHeader));
  }

  @Test
  @DisplayName("given a registration request, when registering, then returns username")
  void givenRegistrationRequest_whenRegistering_thenReturnsUsername() {
    // Given
    String expectedUsername = "newuser";
    when(userService.registerUser(any(RegistrationRequestDto.class))).thenReturn(expectedUsername);

    // When
    String actualUsername = authService.registerUser(registrationRequestDto);

    // Then
    assertNotNull(actualUsername);
    assertEquals(expectedUsername, actualUsername);
    verify(userService).registerUser(registrationRequestDto);
  }

  @Test
  @DisplayName("given an existing user, when registering, then throws UserAlreadyExistsException")
  void givenExistingUser_whenRegistering_thenThrowsUserAlreadyExistsException() {
    // Given
    when(userService.registerUser(any(RegistrationRequestDto.class)))
        .thenThrow(new UserAlreadyExistsException("newuser"));

    // When & Then
    assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(registrationRequestDto));
  }
}
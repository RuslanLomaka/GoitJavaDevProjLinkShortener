package org.decepticons.linkshortener.api.security.service.impl;

import org.decepticons.linkshortener.api.dto.RegistrationRequestDto;
import org.decepticons.linkshortener.api.exceptions.InvalidPasswordException;
import org.decepticons.linkshortener.api.exceptions.UserAlreadyExistsException;
import org.decepticons.linkshortener.api.exceptions.UserNotFoundException;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserAuthServiceImpl class.
 * These tests focus on the user registration and retrieval logic using mocks for dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Authentication Service Unit Tests")
class UserAuthServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserAuthServiceImpl userAuthService;

  private RegistrationRequestDto validRegistrationDto;
  private RegistrationRequestDto invalidPasswordRegistrationDto;
  private User testUser;
  private String rawPassword;
  private String invalidPassword;

  @BeforeEach
  void setUp() {
    rawPassword = "Password123";
    invalidPassword = "short";

    validRegistrationDto = new RegistrationRequestDto();
    validRegistrationDto.setUsername("testuser");
    validRegistrationDto.setPassword(rawPassword);

    invalidPasswordRegistrationDto = new RegistrationRequestDto();
    invalidPasswordRegistrationDto.setUsername("invalidpassworduser");
    invalidPasswordRegistrationDto.setPassword(invalidPassword);

    testUser = new User();
    testUser.setUsername("testuser");
    testUser.setPasswordHash("encodedPassword");
  }

  @Test
  @DisplayName("given a new user, when registering, then save the user successfully")
  void givenNewUser_whenRegistering_thenSavesUserSuccessfully() {
    // Given
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(passwordEncoder.encode(rawPassword)).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // When
    String actualResult = userAuthService.registerUser(validRegistrationDto);

    // Then
    String expectedResult = "testuser";
    assertNotNull(actualResult);
    assertEquals(expectedResult, actualResult);
    verify(userRepository).existsByUsername("testuser");
    verify(passwordEncoder).encode(rawPassword);
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("given an existing user, when registering, then throws UserAlreadyExistsException")
  void givenExistingUser_whenRegistering_thenThrowsUserAlreadyExistsException() {
    // Given
    when(userRepository.existsByUsername(anyString())).thenReturn(true);

    // When & Then
    assertThrows(UserAlreadyExistsException.class, () -> userAuthService.registerUser(validRegistrationDto));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("given an invalid password, when registering, then throws InvalidPasswordException")
  void givenInvalidPassword_whenRegistering_thenThrowsInvalidPasswordException() {
    // Given
    when(userRepository.existsByUsername(anyString())).thenReturn(false);

    // When & Then
    assertThrows(InvalidPasswordException.class, () -> userAuthService.registerUser(invalidPasswordRegistrationDto));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("given an existing username, when finding by username, then returns the user")
  void givenExistingUsername_whenFindingByUsername_thenReturnsUser() {
    // Given
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

    // When
    User actualUser = userAuthService.findByUsername("testuser");

    // Then
    assertNotNull(actualUser);
    assertEquals("testuser", actualUser.getUsername());
    verify(userRepository).findByUsername("testuser");
  }

  @Test
  @DisplayName("given a nonexistent username, when finding by username, then throws UserNotFoundException")
  void givenNonexistentUsername_whenFindingByUsername_thenThrowsUserNotFoundException() {
    // Given
    when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

    // When & Then
    assertThrows(UserNotFoundException.class, () -> userAuthService.findByUsername("nonexistentuser"));
    verify(userRepository).findByUsername("nonexistentuser");
  }
}
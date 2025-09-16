package org.decepticons.linkshortener.api.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PasswordValidator class.
 * These tests ensure that the password complexity rules are correctly enforced.
 */
@DisplayName("Password Validator Unit Tests")
class PasswordValidatorTest {

  @Test
  @DisplayName("given a valid password, when validating, then returns true")
  void givenValidPassword_whenValidating_thenReturnTrue() {
    // Given
    String password1 = "Password123!";
    String password2 = "MySecureP@ssw0rd";
    String password3 = "Another_Long_Password_42";
    // When & Then
    assertTrue(PasswordValidator.isValid(password1));
    assertTrue(PasswordValidator.isValid(password2));
    assertTrue(PasswordValidator.isValid(password3));
  }

  @Test
  @DisplayName("given a password that is too short, when validating, then returns false")
  void givenTooShortPassword_whenValidating_thenReturnFalse() {
    // Given
    String password1 = "short";
    String password2 = "A1!";
    String password3 = "1";
    // When & Then
    assertFalse(PasswordValidator.isValid(password1));
    assertFalse(PasswordValidator.isValid(password2));
    assertFalse(PasswordValidator.isValid(password3));
  }

  @Test
  @DisplayName("given a password with no uppercase letters, when validating, then returns false")
  void givenNoUppercasePassword_whenValidating_thenReturnFalse() {
    // Given
    String password1 = "password123!";
    String password2 = "mypassword_123";

    // When & Then
    assertFalse(PasswordValidator.isValid(password1));
    assertFalse(PasswordValidator.isValid(password2));
  }

  @Test
  @DisplayName("given a password with no lowercase letters, when validating, then returns false")
  void givenNoLowercasePassword_whenValidating_thenReturnFalse() {
    // Given
    String password1 = "PASSWORD123!";
    String password2 = "MYP@SSW0RD_123";

    // When & Then
    assertFalse(PasswordValidator.isValid(password1));
    assertFalse(PasswordValidator.isValid(password2));
  }

  @Test
  @DisplayName("given a password with no digits, when validating, then returns false")
  void givenNoDigitsPassword_whenValidating_thenReturnFalse() {
    // Given
    String password1 = "PasswordTest!";
    String password2 = "MySecurePassword_";

    // When & Then
    assertFalse(PasswordValidator.isValid(password1));
    assertFalse(PasswordValidator.isValid(password2));
  }

  @Test
  @DisplayName("given a null or empty password, when validating, then returns false")
  void givenNullOrEmptyPassword_whenValidating_thenReturnFalse() {
    // Given & When & Then
    assertFalse(PasswordValidator.isValid(null));
    assertFalse(PasswordValidator.isValid(""));
    assertFalse(PasswordValidator.isValid("    "));
  }

}
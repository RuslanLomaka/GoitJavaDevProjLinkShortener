package org.decepticons.linkshortener.api.util;

import java.util.regex.Pattern;

/**
 * Utility class for validating passwords based on predefined rules.
 * This class cannot be instantiated.
 */
public final class PasswordValidator {


  /**
   * The regular expression pattern for password validation.
   * The pattern requires at least one digit, one lowercase letter,
   * one uppercase letter, and a minimum length of 8 characters.
   */
  private static final Pattern PATTERN = Pattern.compile(
      "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$"
  );

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private PasswordValidator() {
    // no-op
  }

  /**
   * Validates a password against the predefined pattern.
   *
   * @param password The password to validate.
   * @return true if the password is valid, false otherwise.
   */
  public static boolean isValid(final String password) {
    return password != null && PATTERN.matcher(password).matches();
  }
}

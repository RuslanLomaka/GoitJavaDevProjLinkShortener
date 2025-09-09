package org.decepticons.linkshortener.api.dto;

import lombok.Data;

/**
 * DTO for user registration request.
 */
@Data
public class RegistrationRequest {

  /**
   * Desired username of the new user.
   */
  private String username;

  /**
   * Desired password of the new user.
   */
  private String password;
}

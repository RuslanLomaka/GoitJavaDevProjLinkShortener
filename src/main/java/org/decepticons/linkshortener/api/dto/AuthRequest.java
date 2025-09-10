package org.decepticons.linkshortener.api.dto;

import lombok.Data;

/**
 * DTO representing login credentials.
 */
@Data
public class AuthRequest {
  /**
   * The username provided for login.
   */
  private String username;

  /**
   * The password provided for login.
   */
  private String password;
}

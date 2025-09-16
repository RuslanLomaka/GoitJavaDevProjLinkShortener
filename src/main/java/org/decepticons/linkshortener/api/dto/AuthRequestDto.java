package org.decepticons.linkshortener.api.dto;

import lombok.Data;

/**
 * DTO representing login credentials.
 */
@Data
public class AuthRequestDto {
  /**
   * The username provided for login. dummmy textField
   */
  private String username;

  /**
   * The password provided for login.
   */
  private String password;
}

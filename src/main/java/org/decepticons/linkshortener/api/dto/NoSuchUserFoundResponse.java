package org.decepticons.linkshortener.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the response when a requested user is not found.
 * This object is typically returned in the response body of an API when a user-related
 * operation fails due to the user not existing in the system.

 * Example JSON:
 * {
 * "username": "Zakhar",
 * "message": "No such user found in the system"
 * }
 */
@AllArgsConstructor
@Getter
@Setter
public class NoSuchUserFoundResponse {

  private String username;

  private String message;


}

package org.decepticons.linkshortener.api.dto;

/**
 * Represents an authentication request containing
 * a username and password.
 *
 * @param username the username of the user
 * @param password the password of the user
 */
public record AuthRequestDto(
    String username,
    String password
) {
}

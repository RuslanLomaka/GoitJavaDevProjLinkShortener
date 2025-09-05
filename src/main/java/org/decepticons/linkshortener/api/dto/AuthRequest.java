package org.decepticons.linkshortener.api.dto;

public record AuthRequest(
        String username,
        String password
) {
}
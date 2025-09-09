package org.decepticons.linkshortener.api.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Data transfer object representing a shortened link.
 * Contains information about the original URL, the generated code,
 * timestamps, click count, status, and the owner's ID.
 *
 * @param id the unique identifier of the link
 * @param code the generated short code for the link
 * @param originalUrl the original URL provided by the user
 * @param createdAt the timestamp when the link was created
 * @param expiresAt the timestamp when the link will expire
 * @param clicks the number of times the short link has been accessed
 * @param status the current status of the link (e.g., ACTIVE, INACTIVE)
 * @param ownerId the unique identifier of the user who owns this link
 */
public record LinkResponse(
    UUID id,
    String code,
    String originalUrl,
    Instant createdAt,
    Instant expiresAt,
    long clicks,
    String status,
    UUID ownerId
) {
}

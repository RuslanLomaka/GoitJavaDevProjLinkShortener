package org.decepticons.linkshortener.api.dto;

import java.time.Instant;
import java.util.UUID;

public record LinkResponse(
        UUID id,
        String code,
        String originalUrl,
        Instant createdAt,
        Instant expiresAt,
        long clicks,
        String status,
        UUID ownerId
) { }

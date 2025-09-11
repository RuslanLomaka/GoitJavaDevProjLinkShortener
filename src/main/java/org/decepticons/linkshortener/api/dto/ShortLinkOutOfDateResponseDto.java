package org.decepticons.linkshortener.api.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * Data Transfer Object (DTO) representing the response when a requested link is out of date.
 * This object is typically returned in the response body of an API when a link-related
 * operation fails due to the short link being obsolete in the system.
 */
@AllArgsConstructor
@Getter
@Setter
public class ShortLinkOutOfDateResponseDto {
  private String shortLink;
  private String message;
  private Instant expiredAt;
}

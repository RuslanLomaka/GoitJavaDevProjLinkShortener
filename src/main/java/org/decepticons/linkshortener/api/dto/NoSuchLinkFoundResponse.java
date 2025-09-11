package org.decepticons.linkshortener.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the response when a requested link is not found.
 * This object is typically returned in the response body of an API when a link-related
 * operation fails due to the short link not existing in the system.

 * Example JSON:
 * {
 * "shortLink": "someShortLink",
 * "message": "No such short link found in the system"
 * }
 */
@AllArgsConstructor
@Getter
@Setter
public class NoSuchLinkFoundResponse {

  private String shortLink;

  private String message;

}

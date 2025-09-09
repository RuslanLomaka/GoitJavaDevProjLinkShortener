package org.decepticons.linkshortener.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


/**
 * Data Transfer Object for receiving a URL from the client.
 * This object is used when creating a new short link.
 * The URL must not be blank and must start with "http://" or "https://".

 * Example JSON:
 * {
 *   "url": "https://www.example.com"
 * }
 */

@Getter
@Setter
public class UrlRequest {
  @Pattern(regexp = "https?://.+", message = "URL must start with http:// or https://")
  @NotBlank
  private String url;

  /**
   * Default constructor required for deserialization by frameworks like Jackson.
   */
  public UrlRequest() {
  }
}

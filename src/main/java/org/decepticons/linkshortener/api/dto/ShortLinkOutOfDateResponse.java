package org.decepticons.linkshortener.api.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
public class ShortLinkOutOfDateResponse {
  private String shortLink;
  private String message;
  private Instant expiredAt;

}

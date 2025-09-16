package org.decepticons.linkshortener.api.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


/**
 * Data Transfer Object (DTO) for updating the expiration date of a shortened link.
 * This object is used to encapsulate the data required to update the expiration
 * date of an existing short link in the system.
 */
@Getter
@Setter
@AllArgsConstructor
public class UpdateLinkExpirationRequestDto {

  @NonNull
  private Instant newExpirationDate;


}

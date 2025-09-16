package org.decepticons.linkshortener.api.service;

import java.time.Instant;
import java.util.UUID;
import org.decepticons.linkshortener.api.dto.LinkResponseDto;
import org.decepticons.linkshortener.api.dto.UrlRequestDto;
import org.springframework.data.domain.Page;

/**
 * Service interface for managing links in the link shortener application.
 * Provides methods for creating, retrieving, updating, and deleting links,
 * as well as tracking link clicks and validating link status.
 */
public interface LinkService {

  /**
   * Creates and persists a new shortened link based on the provided original URL.
   *
   * @param originalUrl the original URL to be shortened
   * @return a LinkResponseDto containing details of the created shortened link
   */
  LinkResponseDto createLink(UrlRequestDto originalUrl);

  /**
   * Increments the click count for the specified link.
   *
   * @param link the LinkResponseDto representing the link to update
   * @return the updated LinkResponseDto with incremented click count
   */
  LinkResponseDto incrementClicks(LinkResponseDto link);

  /**
   * Retrieves a link by its unique short code.
   *
   * @param code the unique short code of the link
   * @return the LinkResponseDto representing the retrieved link
   */
  LinkResponseDto getLinkByCode(String code);

  /**
   * Deactivates the specified link, preventing further access.
   *
   * @param link the LinkResponseDto representing the link to deactivate
   * @return the updated LinkResponseDto with deactivated status
   */
  LinkResponseDto deactivateLink(LinkResponseDto link);

  /**
   * Validates whether the specified link is active and not expired.
   *
   * @param link the LinkResponseDto representing the link to validate
   * @return true if the link is valid (active and not expired), false otherwise
   */
  boolean validateLink(LinkResponseDto link);

  /**
   * Retrieves a paginated list of all links created by the currently authenticated user.
   *
   * @param page the page number to retrieve
   * @param size the number of links per page
   * @return a Page of LinkResponseDto representing the user's links
   */
  Page<LinkResponseDto> getAllMyLinks(int page, int size);

  /**
   * Retrieves a paginated list of all active links created by the currently authenticated user.
   *
   * @param page the page number to retrieve
   * @param size the number of links per page
   * @return a Page of LinkResponseDto representing the user's active links
   */
  Page<LinkResponseDto> getAllMyActiveLinks(int page, int size);

  /**
   * Deletes a link by its unique identifier.
   *
   * @param linkId the UUID of the link to delete
   * @return a confirmation message indicating the result of the deletion
   */
  String deleteLink(UUID linkId);

  /**
   * Updates the expiration date of a link identified by its short code.
   *
   * @param code the unique short code of the link to update
   * @param newExpirationDate the new expiration date to set
   * @return the updated LinkResponseDto with the new expiration date
   */
  LinkResponseDto updateLinkExpiration(String code, Instant newExpirationDate);
}

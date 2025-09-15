package org.decepticons.linkshortener.api.v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.UUID;
import org.decepticons.linkshortener.api.dto.LinkResponseDto;
import org.decepticons.linkshortener.api.dto.UpdateLinkExpirationRequestDto;
import org.decepticons.linkshortener.api.dto.UrlRequestDto;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.service.LinkService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * REST controller for managing short links.
 * Provides endpoints for creating, retrieving, and deleting short links.
 * All operations are performed in the context of the currently authenticated user.
 */
@Tag(name = "Link Management", description = "Operations for managing short links")
@RestController
@RequestMapping("/api/v1/links")
public class LinkCrudController {

  private final LinkService linkService;


  /**
   * Constructs a new {@link LinkCrudController} with the given dependencies.
   *
   * @param linkService the service responsible for link business logic
   */
  public LinkCrudController(LinkService linkService) {
    this.linkService = linkService;

  }

  /**
   * Creates a short URL for the currently authenticated user.
   *
   * @param originalUrl DTO containing the original URL
   * @return DTO with information about the created short URL
   */
  @PostMapping
  @Operation(summary = "Create a short URL for the current user")
  public ResponseEntity<LinkResponseDto> createLink(@Valid @RequestBody UrlRequestDto originalUrl) {
    User user = linkService.getCurrentUser();

    LinkResponseDto link = linkService.createLink(originalUrl, user);

    return ResponseEntity.status(201).body(link);
  }

  /**
   * Retrieves all links of the current user (active and inactive) with pagination.
   *
   * @param page page number (default 0)
   * @param size number of records per page (default 10)
   * @return page of LinkResponseDto
   */
  @GetMapping("/my_all_links")
  @Operation(summary = "Get all links (active and inactive) for the current user")
  public ResponseEntity<Page<LinkResponseDto>> getAllMyLinks(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ResponseEntity.ok(linkService.getAllMyLinks(page, size));
  }

  /**
   * Retrieves only active links of the current user with pagination.
   *
   * @param page page number (default 0)
   * @param size number of records per page (default 10)
   * @return page of LinkResponseDto
   */
  @GetMapping("/my_all_active_links")
  @Operation(summary = "Get all active links for the current user")
  public ResponseEntity<Page<LinkResponseDto>> getAllMyActiveLinks(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ResponseEntity.ok(linkService.getAllMyActiveLinks(page, size));
  }

  /**
   * Deletes a specific link of the current user by its unique ID.
   *
   * @param id unique identifier of the link
   * @return HTTP 204 No Content if deletion was successful
   */
  @DeleteMapping("/delete/{id}")
  @Operation(summary = "Delete a specific link of the current user by its ID")
  public ResponseEntity<Void> deleteLink(@PathVariable UUID id) {
    linkService.deleteLink(id);
    return ResponseEntity.noContent().build();
  }



  /**
   * Updates the expiration date of a specific link identified by its code.
   *
   * @param newExpirationDate DTO containing the new expiration date
   * @param code the short URL code
   * @return DTO with information about the updated link
   */
  @PatchMapping("/{code}")
  @Operation(summary = "Update the expiration date of a specific link by its code")
  public ResponseEntity<LinkResponseDto> updateLinkExpiration(
      @Valid @RequestBody UpdateLinkExpirationRequestDto newExpirationDate,
      @PathVariable String code) {

      return ResponseEntity
          .ok(linkService.updateLinkExpiration(code, newExpirationDate.getNewExpirationDate()));
  }

}

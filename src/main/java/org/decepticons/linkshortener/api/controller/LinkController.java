package org.decepticons.linkshortener.api.controller;


import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;
import org.decepticons.linkshortener.api.dto.LinkResponse;
import org.decepticons.linkshortener.api.dto.UrlRequest;
import org.decepticons.linkshortener.api.exception.NoSuchUserFoundInTheSystem;
import org.decepticons.linkshortener.api.model.Link;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.LinkRepository;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.decepticons.linkshortener.api.service.LinkService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing short URLs.
 * Provides endpoints for creating new short links and redirecting by existing codes.
 */
@RestController
@RequestMapping("/api/links")
public class LinkController {
  private final LinkService linkService;
  private final LinkRepository linkRepository;
  private final UserRepository userRepository;

  /**
   * Constructs a new {@link LinkController} with the given dependencies.
   *
   * @param linkService the service responsible for link business logic
   * @param linkRepository the repository for accessing {@link Link} entities
   * @param userRepository the repository for accessing {@link User} entities
   */
  public LinkController(LinkService linkService,
                        LinkRepository linkRepository,
                        UserRepository userRepository) {
    this.linkService = linkService;
    this.linkRepository = linkRepository;
    this.userRepository = userRepository;
  }

  /**
   * Creates a new short URL for an authenticated user.
   *
   * @param originalUrl DTO containing the original URL
   * @return DTO with information about the created short URL
   */
  @PostMapping
  public ResponseEntity<LinkResponse> createLink(
      @Valid @RequestBody UrlRequest originalUrl) {

    String userName = SecurityContextHolder.getContext()
        .getAuthentication()
        .getName();

    User user = userRepository.findByUsername(userName)
        .orElseThrow(() -> new NoSuchUserFoundInTheSystem(
            "No such user found in the system: " + userName,
            userName
        ));

    LinkResponse link = linkService.createLink(originalUrl, user);

    return ResponseEntity.status(201).body(link);
  }

  /**
   * Redirects to the original URL by the given short code.
   *
   * @param code the short URL code
   * @return a redirect response or status code indicating an error
   */
  @GetMapping("/{code}")
  public ResponseEntity<String> redirect(@PathVariable String code) {
    Optional<Link> optionalLink = linkRepository.findByCode(code);

    if (optionalLink.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Link link = optionalLink.get();

    if (!linkService.isLinkActive(link)) {
      return ResponseEntity.status(410).body("Link expired or inactive");
    }

    linkService.incrementClicks(link);

    return ResponseEntity.status(302)
        .header("Location", link.getOriginalUrl())
        .build();
  }

  /**
   * Retrieves a paginated list of all user's links including active and inactive ones.
   *
   * @param page the page number to retrieve
   * @param size the number of records per page
   * @return {@link ResponseEntity} containing a {@link Page} of {@link LinkResponse} objects
   */
  @GetMapping("/my_all_links")
  public ResponseEntity<Page<LinkResponse>> getAllMyLinks(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ResponseEntity.ok(linkService.getAllMyLinks(page, size));
  }

  /**
   * Retrieves a paginated list of the current user's active links only.
   *
   * @param page the page number to retrieve
   * @param size the number of records per page
   * @return a {@link ResponseEntity} containing a {@link Page} of {@link LinkResponse} objects
   */
  @GetMapping("/my_all_active_links")
  public ResponseEntity<Page<LinkResponse>> getAllMyActiveLinks(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ResponseEntity.ok(linkService.getAllMyActiveLinks(page, size));
  }

  /**
   * Deletes a specific link belonging to the current user by its unique ID.
   *
   * @param id the unique identifier of the link to delete
   * @return a {@link ResponseEntity} with HTTP status 204 (No Content)
   *     if the deletion was successful
   */
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> deleteLink(@PathVariable UUID id) {
    linkService.deleteLink(id);
    return ResponseEntity.noContent().build();
  }
}

package org.decepticons.linkshortener.api.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import org.decepticons.linkshortener.api.dto.LinkResponseDto;
import org.decepticons.linkshortener.api.dto.UrlRequestDto;
import org.decepticons.linkshortener.api.exception.NoSuchUserFoundInTheSystemException;
import org.decepticons.linkshortener.api.exception.ShortLinkIsOutOfDateException;
import org.decepticons.linkshortener.api.model.LinkStatus;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.decepticons.linkshortener.api.service.CacheInspectionService;
import org.decepticons.linkshortener.api.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing short URLs.
 * Provides endpoints for creating new short links and redirecting by existing codes.
 */
@RestController
@RequestMapping("/api/links")
public class LinkController {

  private final LinkService linkService;
  private final UserRepository userRepository;

  @Autowired
  private CacheInspectionService cacheInspectionService;

  /**
   * Constructs a new {@link LinkController} with the given dependencies.
   *
   * @param linkService    the service responsible for link business logic
   * @param userRepository the repository for accessing {@link User} entities
   */
  public LinkController(LinkService linkService,
                        UserRepository userRepository) {
    this.linkService = linkService;
    this.userRepository = userRepository;
  }

  /**
   * Creates a new short URL for an authenticated user.
   *
   * @param originalUrl DTO containing the original URL
   * @return DTO with information about the created short URL
   */
  @PostMapping
  public ResponseEntity<LinkResponseDto> createLink(
      @Valid @RequestBody UrlRequestDto originalUrl) {

    String userName = SecurityContextHolder.getContext()
        .getAuthentication()
        .getName();

    User user = userRepository.findByUsername(userName)
        .orElseThrow(() -> new NoSuchUserFoundInTheSystemException(
            "No such user found in the system: " + userName,
            userName
        ));

    LinkResponseDto link = linkService.createLink(originalUrl, user);

    return ResponseEntity.status(201).body(link);
  }

  /**
   * Redirects to the original URL by the given short code.
   *
   * @param code the short URL code
   */
  @GetMapping("/{code}")
  public void redirect(@PathVariable String code, HttpServletResponse response) throws IOException {

    LinkResponseDto link = linkService.accessLink(code);

    if (link.status().equalsIgnoreCase(LinkStatus.INACTIVE.name())) {
      throw new ShortLinkIsOutOfDateException(
          "Short link is out of date: " + code,
          code,
          link.expiresAt()
      );
    }

    response.sendRedirect(link.originalUrl());
  }

  /**
   * Endpoint to inspect the contents of the short links cache.
   * This is primarily for debugging and monitoring purposes.
   * </p>
   * Note: In a production environment, access to this endpoint should be
   * restricted to authorized personnel only, as it may expose sensitive data.
   */
  @GetMapping("/cache")
  public void inspectCache() {
    cacheInspectionService.printCache("shortLinksCache");
  }
}

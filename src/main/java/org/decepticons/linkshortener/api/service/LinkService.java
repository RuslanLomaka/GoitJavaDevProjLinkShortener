package org.decepticons.linkshortener.api.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;
import org.decepticons.linkshortener.api.dto.LinkResponseDto;
import org.decepticons.linkshortener.api.dto.UrlRequestDto;
import org.decepticons.linkshortener.api.exception.NoSuchShortLinkFoundInTheSystemException;
import org.decepticons.linkshortener.api.exception.NoSuchUserFoundInTheSystemException;
import org.decepticons.linkshortener.api.model.Link;
import org.decepticons.linkshortener.api.model.LinkStatus;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.LinkRepository;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service responsible for creating and maintaining {@link Link} entities.
 * Provides operations for link creation, click tracking, simple DTO mapping,
 * and basic liveness checks (active + not expired).
 * </p>
 */


@Service
public class LinkService {

  private final LinkRepository linkRepository;
  private final UserRepository userRepository;
  private final Random random = new Random();


  /**
   * Creates a new {@code LinkService}.
   *
   * @param linkRepository repository used to persist and load {@link Link} entities
   */

  public LinkService(LinkRepository linkRepository, UserRepository userRepository) {
    this.linkRepository = linkRepository;
    this.userRepository = userRepository;
  }



  /**
   * Creates and persists a new {@link Link}.
   *
   * @param originalUrl the original long URL to be shortened
   * @param owner       the user who owns the link (must be non-null and managed)
   * @return a {@link LinkResponseDto} representing the newly created link
   */
  @Transactional
  public LinkResponseDto createLink(UrlRequestDto originalUrl, User owner) {
    Link link = new Link();
    link.setOriginalUrl(originalUrl.getUrl());
    link.setOwner(owner);

    link.setCode(generateRandomCode());
    link.setExpiresAt(Instant.now().plus(2, ChronoUnit.DAYS));
    link.setStatus(LinkStatus.ACTIVE);

    linkRepository.save(link);

    return mapToResponse(link);
  }


  /**
   * Increments the click counter of the given link and persists the change.
   * This should be invoked whenever the shortened URL is accessed.
   * Internally, the entity updates its {@code lastAccessedAt} timestamp.
   * </p>
   *
   * @param link the link whose click counter should be incremented
   */
  @Transactional
  @CachePut(value = "shortLinksCache", key = "#link.code")
  public void incrementClicks(LinkResponseDto link) {
    Link linkByCode = linkRepository.findByCode(link.code())
        .orElseThrow(() -> new NoSuchShortLinkFoundInTheSystemException(
            "No such short link found in the system: " + link.code(),
            link.code()
        ));
    linkByCode.incrementClicks();
    linkRepository.save(linkByCode);
  }


  /**
   * Maps a {@link Link} JPA entity to a transport-friendly {@link LinkResponseDto}.
   *
   * @param link the entity to map
   * @return a response DTO with the most relevant fields
   */
  public LinkResponseDto mapToResponse(Link link) {
    return new LinkResponseDto(

        link.getId(),
        link.getCode(),
        link.getOriginalUrl(),
        link.getCreatedAt(),
        link.getExpiresAt(),
        link.getClicks(),
        link.getStatus().name(),
        link.getOwner().getId()
    );
  }



  /**
   * Generates a pseudo-random short code of fixed length .
   *
   * @return a new short code (e.g., {@code "aZ3fQ1"})
   */
  private String generateRandomCode() {
    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 6; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }


  /**
   * Retrieves a {@link Link} entity by its short code.
   *
   * @param code short link code.
   * @return Optional<Link>
   */
  @Cacheable(value = "shortLinksCache", key = "#code")
  public LinkResponseDto getLinkByCode(String code) {
    Link link = linkRepository.findByCode(code)
        .orElseThrow(() -> new NoSuchShortLinkFoundInTheSystemException(
        "No such short link found in the system: " + code,
        code
    ));

    return mapToResponse(link);

  }

  /**
   * Accesses a link by its code, increments click count if active, and updates status if expired.
   *
   * @param code the short code of the link to access
   * @return a {@link LinkResponseDto} representing the accessed link
   * @throws NoSuchShortLinkFoundInTheSystemException if no link with the given code exists
   */
  @Transactional
  @CachePut(value = "shortLinksCache", key = "#code")
  public LinkResponseDto accessLink(String code) {

    Link link = linkRepository.findByCode(code)
        .orElseThrow(() -> new NoSuchShortLinkFoundInTheSystemException(
            "No such short link found in the system: " + code, code));


    boolean active = link.getStatus() == LinkStatus.ACTIVE
        && (link.getExpiresAt() == null || link.getExpiresAt().isAfter(Instant.now()));

    if (!active) {
      link.setStatus(LinkStatus.INACTIVE);
      linkRepository.save(link);
    } else {

      link.incrementClicks();
      linkRepository.save(link);
    }


    return mapToResponse(link);
  }

  /**
   * Retrieves all links of the currently authenticated user with pagination.
   *
   * @param page the page number to retrieve (0-based)
   * @param size the number of records per page
   * @return a {@link Page} of {@link LinkResponseDto} objects representing all user's links
   */
  public Page<LinkResponseDto> getAllMyLinks(int page, int size) {
    UUID userId = getCurrentUserId();
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return linkRepository.findAllByOwnerId(userId, pageable)
        .map(this::mapToResponse);
  }

  /**
   * Retrieves all active links of the currently authenticated user with pagination.
   *
   * @param page the page number to retrieve (0-based)
   * @param size the number of records per page
   * @return a {@link Page} of {@link LinkResponseDto} objects representing active user's links
   */
  public Page<LinkResponseDto> getAllMyActiveLinks(int page, int size) {
    UUID userId = getCurrentUserId();
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return linkRepository.findAllByOwnerIdAndStatus(userId, LinkStatus.ACTIVE, pageable)
        .map(this::mapToResponse);
  }

  /**
   * Marks a link as inactive (soft delete) if it belongs to the currently authenticated user.
   *
   * @param linkId the unique identifier of the link to delete
   */
  public void deleteLink(UUID linkId) {
    UUID userId = getCurrentUserId();
    linkRepository.findById(linkId)
        .filter(link -> link.getOwner().getId().equals(userId))
        .ifPresent(link -> {
          link.setStatus(LinkStatus.INACTIVE);
          linkRepository.save(link);
        });
  }

  /**
   * Retrieves the UUID of the currently authenticated user from the security context.
   *
   * @return the UUID of the authenticated user
   */
  private UUID getCurrentUserId() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NoSuchUserFoundInTheSystemException(
            "No such user found in the system: " + username,
            username
        ));
    return user.getId();
  }
}


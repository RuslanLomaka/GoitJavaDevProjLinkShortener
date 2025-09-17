package org.decepticons.linkshortener.api.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;
import org.decepticons.linkshortener.api.dto.LinkResponseDto;
import org.decepticons.linkshortener.api.dto.UrlRequestDto;
import org.decepticons.linkshortener.api.exception.InvalidExpirationDateException;
import org.decepticons.linkshortener.api.exception.NoSuchShortLinkFoundInTheSystemException;
import org.decepticons.linkshortener.api.model.Link;
import org.decepticons.linkshortener.api.model.LinkStatus;
import org.decepticons.linkshortener.api.repository.LinkRepository;
import org.decepticons.linkshortener.api.service.LinkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service responsible for creating and maintaining {@link Link} entities.
 * Provides operations for link creation, click tracking, simple DTO mapping,
 * and basic liveness checks (active + not expired).
 * </p>
 */

@Service
public class LinkServiceImpl implements LinkService {

  @Value("${link.expiration-days}")
  private long linkExpirationDays;

  private final LinkRepository linkRepository;
  private final UserServiceImpl userServiceImpl;
  private final Random random = new Random();

  /**
   * Creates a new {@code LinkService}.
   *
   * @param linkRepository repository used to persist and load {@link Link} entities
   */

  public LinkServiceImpl(LinkRepository linkRepository,
                         UserServiceImpl userServiceImpl) {
    this.linkRepository = linkRepository;
    this.userServiceImpl = userServiceImpl;
  }

  /**
   * Creates and persists a new {@link Link}.
   *
   * @param originalUrl the original long URL to be shortened
   * @return a {@link LinkResponseDto} representing the newly created link
   */
  @Override
  @Transactional
  public LinkResponseDto createLink(UrlRequestDto originalUrl) {
    Link link = new Link();
    link.setOriginalUrl(originalUrl.getUrl());
    link.setOwner(userServiceImpl.getCurrentUser());

    link.setCode(generateRandomCode());
    link.setExpiresAt(Instant.now().plus(linkExpirationDays, ChronoUnit.DAYS));
    link.setStatus(LinkStatus.ACTIVE);

    Link saved = linkRepository.save(link);

    return mapToResponse(saved);
  }

  /**
   * Increments the click counter of the given link and persists the change.
   * This should be invoked whenever the shortened URL is accessed.
   * Internally, the entity updates its {@code lastAccessedAt} timestamp.
   * </p>
   *
   * @param link the link whose click counter should be incremented
   */
  @Override
  @Transactional
  public LinkResponseDto incrementClicks(LinkResponseDto link) {

    int affectedRows = linkRepository.incrementClicksByCodeNative(link.code());

    if (affectedRows == 0) {
      throw new NoSuchShortLinkFoundInTheSystemException(
          "No such short link found in the system: " + link.code(),
          link.code()
      );
    }
    Link updatedLink = linkRepository.findByCode(link.code())
        .orElseThrow(() -> new NoSuchShortLinkFoundInTheSystemException(
            "No such short link found in the system: " + link.code(),
            link.code()
        ));

    return mapToResponse(updatedLink);
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
  @Override
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
   * Deactivates a link by setting its status to INACTIVE.
   *
   * @param link the link to deactivate
   * @return the updated {@link LinkResponseDto} with status set to INACTIVE
   */

  @Override
  @CacheEvict(value = "shortLinksCache", key = "#result.code")
  public LinkResponseDto deactivateLink(LinkResponseDto link) {
    Link linkByCode = linkRepository.findByCode(link.code())
        .orElseThrow(() -> new NoSuchShortLinkFoundInTheSystemException(
            "No such short link found in the system: " + link.code(),
            link.code()
        ));
    linkByCode.setStatus(LinkStatus.INACTIVE);
    Link saved = linkRepository.save(linkByCode);
    return mapToResponse(saved);
  }

  /**
   * Validates if a link is active and not expired.
   *
   * @param link the link to validate
   * @return {@code true} if the link is active and not expired; {@code false} otherwise
   */
  @Override
  public boolean validateLink(LinkResponseDto link) {
    return link.status().equalsIgnoreCase(LinkStatus.ACTIVE.toString())
        && (link.expiresAt() == null || link.expiresAt().isAfter(Instant.now()));
  }

  /**
   * Retrieves all links of the currently authenticated user with pagination.
   *
   * @param page the page number to retrieve (0-based)
   * @param size the number of records per page
   * @return a {@link Page} of {@link LinkResponseDto} objects representing all user's links
   */
  @Override
  public Page<LinkResponseDto> getAllMyLinks(int page, int size) {
    UUID userId = userServiceImpl.getCurrentUserId();
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
  @Override
  public Page<LinkResponseDto> getAllMyActiveLinks(int page, int size) {
    UUID userId = userServiceImpl.getCurrentUserId();
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return linkRepository.findAllByOwnerIdAndStatus(userId, LinkStatus.ACTIVE, pageable)
        .map(this::mapToResponse);
  }

  /**
   * Deletes a link from the database if it belongs to the currently authenticated user.
   *
   * @param linkId the unique identifier of the link to delete
   */
  @Transactional
  @Override
  @CacheEvict(value = "shortLinksCache", key = "#result")
  public String deleteLink(UUID linkId) {
    UUID currentUserId = userServiceImpl.getCurrentUserId();

    Link link = linkRepository.findById(linkId)
        .orElseThrow(() -> new NoSuchShortLinkFoundInTheSystemException(
            "No such short link found in the system", linkId.toString()
        ));

    if (!link.getOwner().getId().equals(currentUserId)) {
      throw new AccessDeniedException("You are not allowed to delete this link");
    }

    linkRepository.delete(link);

    return link.getCode();
  }

  /**
   * Retrieves the UUID of the currently authenticated user from the security context.
   *
   * @return the UUID of the authenticated user
   */

  @CachePut(value = "shortLinksCache", key = "#code")
  @Override
  public LinkResponseDto updateLinkExpiration(String code, Instant newExpirationDate) {
    Link link = linkRepository.findByCode(code)
        .orElseThrow(() -> new NoSuchShortLinkFoundInTheSystemException(
            "No such short link found in the system: " + code,
            code
        ));

    if (!link.getOwner().getId().equals(userServiceImpl.getCurrentUserId())) {
      throw new AccessDeniedException("You are not allowed to update this link");
    }

    if (newExpirationDate.isBefore(Instant.now())) {
      throw new InvalidExpirationDateException(
          "Expiration date must be in the future",
          newExpirationDate
      );
    }

    link.setExpiresAt(newExpirationDate);
    Link saved = linkRepository.save(link);

    return mapToResponse(saved);
  }

}

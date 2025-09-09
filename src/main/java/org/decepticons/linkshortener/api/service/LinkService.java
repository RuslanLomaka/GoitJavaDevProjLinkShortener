package org.decepticons.linkshortener.api.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import org.decepticons.linkshortener.api.dto.LinkResponse;
import org.decepticons.linkshortener.api.dto.UrlRequest;
import org.decepticons.linkshortener.api.model.Link;
import org.decepticons.linkshortener.api.model.LinkStatus;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.LinkRepository;
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
  private final Random random = new Random();

  /**
   * Creates a new {@code LinkService}.
   *
   * @param linkRepository repository used to persist and load {@link Link} entities
   */

  public LinkService(LinkRepository linkRepository) {
    this.linkRepository = linkRepository;
  }



  /**
   * Creates and persists a new {@link Link}.
   *
   * @param originalUrl the original long URL to be shortened
   * @param owner       the user who owns the link (must be non-null and managed)
   * @return a {@link LinkResponse} representing the newly created link
   */
  @Transactional
  public LinkResponse createLink(UrlRequest originalUrl, User owner) {
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
  public void incrementClicks(Link link) {
    link.incrementClicks();
    linkRepository.save(link);
  }


  /**
   * Maps a {@link Link} JPA entity to a transport-friendly {@link LinkResponse}.
   *
   * @param link the entity to map
   * @return a response DTO with the most relevant fields
   */
  public LinkResponse mapToResponse(Link link) {
    return new LinkResponse(

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
   * Checks whether a link is currently active: status is {@link LinkStatus#ACTIVE}
   * and the expiration moment (if any) is in the future.
   *
   * @param link the link to evaluate
   * @return {@code true} if the link is active and not expired; {@code false} otherwise
   */
  public boolean isLinkActive(Link link) {
    return link.getStatus() == LinkStatus.ACTIVE

        &&
        (link.getExpiresAt() == null || link.getExpiresAt().isAfter(Instant.now()));
  }


}

package org.decepticons.linkshortener.api.repository;

import java.util.Optional;
import org.decepticons.linkshortener.api.model.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides methods to interact with the database for Link entities.
 */
@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

  /**
   * Finds a link by generated short URL.
   *
   * @param code short URL code
   * @return an optional containing the link if found
   */
  Optional<Link> findByCode(String code);

  /**
   * Verifies if a link exists by generated short URL.
   *
   * @param code short URL code
   * @return true if the link exists, false otherwise
   */
  boolean existsByCode(String code);

  /**
   * Finds all links by owner id.
   *
   * @param ownerId  owner id
   * @param pageable pagination information
   * @return a page of links
   */
  Page<Link> findAllByOwnerId(Long ownerId, Pageable pageable);

  /**
   * Finds all links by owner id and status.
   *
   * @param ownerId  owner id
   * @param status   link status
   * @param pageable pagination information
   * @return a page of links
   */
  Page<Link> findAllByOwnerIdAndStatus(Long ownerId, String status, Pageable pageable);
}
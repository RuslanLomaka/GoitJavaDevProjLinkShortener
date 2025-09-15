package org.decepticons.linkshortener.api.repository;

import java.util.Optional;
import java.util.UUID;
import org.decepticons.linkshortener.api.model.Link;
import org.decepticons.linkshortener.api.model.LinkStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Link} entities.
 * Provides CRUD operations and custom queries for retrieving links by code, owner, and status.
 */
@Repository
public interface LinkRepository extends JpaRepository<Link, UUID> {

  /**
   * Finds a link by its unique short code.
   *
   * @param code the short code of the link
   * @return an {@link Optional} containing the {@link Link} if found, or empty if not found
   */
  Optional<Link> findByCode(String code);

  /**
   * Checks whether a link with the specified short code exists.
   *
   * @param code the short code to check
   * @return {@code true} if a link with the code exists, {@code false} otherwise
   */
  boolean existsByCode(String code);

  /**
   * Retrieves all links belonging to a specific user with pagination support.
   *
   * @param ownerId  the UUID of the user (owner)
   * @param pageable the {@link Pageable} object specifying page number, size, and sorting
   * @return a {@link Page} of {@link Link} objects belonging to the specified user
   */
  Page<Link> findAllByOwnerId(UUID ownerId, Pageable pageable);

  /**
   * Retrieves all links belonging to a specific user that have a certain status,
   *     with pagination support.
   *
   * @param ownerId  the UUID of the user (owner)
   * @param status   the {@link LinkStatus} to filter links by (e.g., ACTIVE, INACTIVE)
   * @param pageable the {@link Pageable} object specifying page number, size, and sorting
   * @return a {@link Page} of {@link Link} objects belonging to the user with the specified status
   */
  Page<Link> findAllByOwnerIdAndStatus(UUID ownerId, LinkStatus status, Pageable pageable);


  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(value = """
    UPDATE links
    SET clicks = clicks + 1, last_accessed_at = now()
    WHERE code = :code
""", nativeQuery = true)
  int incrementClicksByCodeNative(@Param("code") String code);
}
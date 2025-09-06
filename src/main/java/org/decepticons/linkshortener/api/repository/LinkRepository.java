package org.decepticons.linkshortener.api.repository;

import org.decepticons.linkshortener.api.model.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

    //finding the link by uuid
    Optional<Link> findByCode(String code);

    //check if the link with this uuid exists
    boolean existsByCode(String code);

    //get all user links with pagination
    Page<Link> findAllByOwnerId(Long ownerId, Pageable pageable);

    //get all links of a user with a certain status + pagination
    Page<Link> findAllByOwnerIdAndStatus(Long ownerId, String status, Pageable pageable);
}
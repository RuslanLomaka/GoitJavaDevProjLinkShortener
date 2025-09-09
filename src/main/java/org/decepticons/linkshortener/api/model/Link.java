package org.decepticons.linkshortener.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Domain entity representing a shortened link owned by a {@link User}.
 * Each link has a stable UUID {@link #id} as the technical identifier and a
 * human-facing short {@link #code} that acts as the redirect token. The original
 * destination is stored in {@link #originalUrl}. Lifecycle/state is tracked via
 * {@link #status}, and optional expiration via {@link #expiresAt}. Creation time
 * is recorded in {@link #createdAt}.
 * </p>
 *
 * <h2>Constraints & Indexes</h2>
 * <ul>
 *   <li>{@link #code} is unique and length-limited (6–8 chars) for compact URLs.</li>
 *   <li>Indexes typically exist on: {@code code} (unique), {@code owner_id},
 *       {@code status}, {@code expires_at} to speed up lookups and cleanup jobs.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <ul>
 *   <li>Resolve by {@link #code} to perform an HTTP redirect to {@link #originalUrl}.</li>
 *   <li>Inactive/expired links ({@link LinkStatus}) must not redirect.</li>
 * </ul>
 *
 * @author Ruslan Lomaka
 * @since 1.0
 */
@Getter
@Entity
@Table(
    name = "links",
    uniqueConstraints = @UniqueConstraint(name = "uk_links_code", columnNames = "code"),
    indexes = {
        @Index(name = "idx_links_owner_id", columnList = "owner_id"),
        @Index(name = "idx_links_status", columnList = "status"),
        @Index(name = "idx_links_expires_at", columnList = "expires_at")
    }
)

public class Link {

  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Setter
  @Column(name = "code", nullable = false, length = 12)
  private String code; // 6–8 chars generated; room for future

  @Setter
  @Column(name = "original_url", nullable = false, columnDefinition = "text")
  private String originalUrl;

  @Setter
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_links_owner"))
  private User owner;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Setter
  @Column(name = "expires_at")
  private Instant expiresAt;

  @Setter
  @Column(name = "clicks", nullable = false)
  private long clicks = 0L;

  @Setter
  @Column(name = "last_accessed_at")
  private Instant lastAccessedAt;

  @Setter
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 16)
  private LinkStatus status = LinkStatus.ACTIVE;


  /**
   * Increments the number of clicks associated with this link
   * and updates the {@code lastAccessedAt} timestamp.
   * This method should be called whenever the shortened URL is
   * accessed, ensuring both the click counter and the last access
   * time are refreshed.
   * </p>
   */
  public void incrementClicks() {
    this.clicks++;
    this.lastAccessedAt = Instant.now();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Link that)) {
      return false;
    }
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}

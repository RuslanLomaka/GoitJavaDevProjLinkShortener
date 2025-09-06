package org.decepticons.linkshortener.api.model;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "links",
        uniqueConstraints = @UniqueConstraint(name = "uk_links_code", columnNames = "code"),
        indexes = {
                @Index(name = "idx_links_owner_id", columnList = "owner_id"),
                @Index(name = "idx_links_status", columnList = "status"),
                @Index(name = "idx_links_expires_at", columnList = "expires_at")
        })
public class Link {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 12)
    private String code; // 6–8 chars generated; room for future

    @Column(name = "original_url", nullable = false, columnDefinition = "text")
    private String originalUrl;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_links_owner"))
    private User owner;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "clicks", nullable = false)
    private long clicks = 0L;

    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private LinkStatus status = LinkStatus.ACTIVE;

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public long getClicks() { return clicks; }
    public void setClicks(long clicks) { this.clicks = clicks; }
    public Instant getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(Instant lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
    public LinkStatus getStatus() { return status; }
    public void setStatus(LinkStatus status) { this.status = status; }

    public void incrementClicks() {
        this.clicks++;
        this.lastAccessedAt = Instant.now();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Link that)) return false;
        return Objects.equals(id, that.id);
    }
    @Override public int hashCode() {
        return Objects.hash(id);
    }
}

package org.decepticons.linkshortener.api.model;

/**
 * Enumeration of possible lifecycle states for a {@link Link}.
 * Used to determine whether a shortened link can still be resolved
 * or if it should be blocked/ignored by the system.
 * </p>
 *
 * <ul>
 *   <li>{@code ACTIVE} – Link is valid and redirects normally.</li>
 *   <li>{@code INACTIVE} – Link is temporarily disabled and must not redirect.</li>
 * </ul>
 *
 * @since 1.0
 */
public enum LinkStatus {
  ACTIVE, INACTIVE
}

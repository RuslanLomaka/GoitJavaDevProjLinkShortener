package org.decepticons.linkshortener.api.model;

/**
 * Enumeration of possible states for a {@link User}.
 * Determines whether a user account is allowed to interact with
 * the system or is restricted.
 * </p>
 *
 * <ul>
 *   <li>{@code ACTIVE} – User account is active and allowed to use the system.</li>
 *   <li>{@code LOCKED} – User account is disabled due to violations or admin action.</li>
 * </ul>
 *
 * @since 1.0
 */

public enum UserStatus {
  ACTIVE, LOCKED
}
package org.decepticons.linkshortener.api.security.model;

import java.io.Serial;
import java.util.Collection;
import lombok.Getter;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.model.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Adapter class to wrap our domain User into Spring Security's UserDetails.
 */
@Getter
public final class CustomUserDetails implements UserDetails {

  /**
   * The serial version UID for serialization.
   */
  @Serial
  private static final long serialVersionUID = 1L;

  /** Domain user wrapped by this adapter. */
  private final transient User user;

  /**
   * Constructs a CustomUserDetails wrapping the given domain User.
   *
   * @param userParam the domain user to wrap
   */
  public CustomUserDetails(final User userParam) {
    this.user = userParam;
  }

  /**
   * Returns the authorities granted to the user.
   *
   * @return collection of granted authorities (roles)
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return user.getRoles();
  }

  /**
   * Returns the password used to authenticate the user.
   *
   * @return the user's password hash
   */
  @Override
  public String getPassword() {
    return user.getPasswordHash();
  }

  /**
   * Returns the username used to authenticate the user.
   *
   * @return the user's username
   */
  @Override
  public String getUsername() {
    return user.getUsername();
  }

  /**
   * Indicates whether the user's account has expired.
   *
   * @return true if the account is not expired
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user's account is locked.
   *
   * @return true if the user is not LOCKED
   */
  @Override
  public boolean isAccountNonLocked() {
    return user.getStatus() != null && user.getStatus() != UserStatus.LOCKED;
  }

  /**
   * Indicates whether the user's credentials are expired.
   *
   * @return true if credentials are valid
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user is enabled.
   * Here we consider ACTIVE as enabled, LOCKED as disabled.
   *
   * @return true if user is ACTIVE
   */
  @Override
  public boolean isEnabled() {
    return user.getStatus() != null && user.getStatus() == UserStatus.ACTIVE;
  }
}

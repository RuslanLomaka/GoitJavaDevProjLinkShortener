package org.decepticons.linkshortener.api.security.model;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import org.decepticons.linkshortener.api.model.User;
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
   * @return collection of granted authorities
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // For this example, we return a single, hardcoded role.
    return Collections.singletonList(
        () -> "ROLE_" + user.getStatus().name()
    );
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
   * Subclasses may override to change the expiration logic.
   *
   * @return true if the account is not expired
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user's account is locked.
   * Subclasses may override to change the lock logic.
   *
   * @return true if the account is not locked
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Indicates whether the user's credentials are expired.
   * Subclasses may override to change the credential expiration logic.
   *
   * @return true if the credentials are valid
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user's account is enabled.
   *
   * @return true if the user's status is ACTIVE
   */
  @Override
  public boolean isEnabled() {
    return user.getStatus().name().equals("ACTIVE");
  }
}

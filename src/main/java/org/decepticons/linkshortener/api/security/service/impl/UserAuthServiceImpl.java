package org.decepticons.linkshortener.api.security.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.decepticons.linkshortener.api.dto.AuthRequest;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.decepticons.linkshortener.api.security.service.UserAuthService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserAuthService} that handles user registration
 * and retrieval by username. Uses {@link UserRepository} for data access
 * and {@link PasswordEncoder} for hashing passwords.
 */
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

  /** Repository for user data access. */
  private final UserRepository userRepository;

  /** Encoder for hashing user passwords. */
  private final PasswordEncoder passwordEncoder;

  /**
   * Registers a new user with the given username and password.
   * Throws an exception if the username already exists.
   *
   * @param request the authentication request containing username and password
   * @return the username of the newly registered user
   */
  @Override
  @Transactional
  public String registerUser(final AuthRequest request) {
    String username = request.username();

    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException(
          "User with username '%s' already exists".formatted(username)
      );
    }

    User newUser = new User();
    newUser.setUsername(username);
    newUser.setPasswordHash(passwordEncoder.encode(request.password()));

    userRepository.save(newUser);

    return newUser.getUsername();
  }

  /**
   * Finds a user by their username.
   *
   * @param username the username to search for
   * @return the User entity
   * @throws UsernameNotFoundException if no user is found
   */
  @Override
  public User findByUsername(final String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(
            "User not found with username: " + username)
        );
  }
}

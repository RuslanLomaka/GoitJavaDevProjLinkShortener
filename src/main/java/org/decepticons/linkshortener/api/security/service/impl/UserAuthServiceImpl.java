package org.decepticons.linkshortener.api.security.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.decepticons.linkshortener.api.exception.InvalidPasswordException;
import org.decepticons.linkshortener.api.exception.UserAlreadyExistsException;
import org.decepticons.linkshortener.api.exception.UserNotFoundException;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.decepticons.linkshortener.api.security.service.UserAuthService;
import org.decepticons.linkshortener.api.util.PasswordValidator;
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
   * Registers a new user.
   * Throws an exception if the username already exists.
   *
   * @param newUser the User domain object to be registered
   * @return the newly registered User object
   */
  @Override
  @Transactional
  public User registerUser(final User newUser) {
    String username = newUser.getUsername();
    String password = newUser.getPasswordHash();

    if (userRepository.existsByUsername(username)) {
      throw new UserAlreadyExistsException(username);
    }

    if (!PasswordValidator.isValid(password)) {
      throw new InvalidPasswordException(
          "Password does not meet complexity requirements"
      );
    }

    newUser.setPasswordHash(passwordEncoder.encode(password));
    userRepository.save(newUser);

    return newUser;
  }

  /**
   * Finds a user by their username.
   *
   * @param username the username to search for
   * @return the User entity
   * @throws UserNotFoundException if no user is found
   */
  @Override
  public User findByUsername(final String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException(username)
        );
  }
}

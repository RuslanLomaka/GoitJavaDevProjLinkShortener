package org.decepticons.linkshortener.api.service.impl;

import java.util.UUID;
import org.decepticons.linkshortener.api.exception.NoSuchUserFoundInTheSystemException;
import org.decepticons.linkshortener.api.exception.UserNotFoundException;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.decepticons.linkshortener.api.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service implementation for retrieving information about the currently authenticated user.
 */
@Service
public class UserServiceImpl implements UserService {

  /**
   * Constructs a new UserServiceImpl with the given UserRepository.
   *
   * @param userRepository the repository used to access user data
   */

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public final UserRepository userRepository;

  @Override
  public UUID getCurrentUserId() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NoSuchUserFoundInTheSystemException(
            "No such user found in the system: " + username,
            username
        ));
    return user.getId();

  }

  @Override
  public User getCurrentUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new NoSuchUserFoundInTheSystemException(
            "No such user found in the system: " + username,
            username
        ));
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

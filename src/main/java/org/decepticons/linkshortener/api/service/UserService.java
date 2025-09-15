package org.decepticons.linkshortener.api.service;

import org.decepticons.linkshortener.api.exception.NoSuchUserFoundInTheSystemException;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

  public UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UUID getCurrentUserId() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NoSuchUserFoundInTheSystemException(
            "No such user found in the system: " + username,
            username
        ));
    return user.getId();

  }

  public User getCurrentUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new NoSuchUserFoundInTheSystemException(
            "No such user found in the system: " + username,
            username
        ));
  }



}

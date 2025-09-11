package org.decepticons.linkshortener.api.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.decepticons.linkshortener.api.exceptions.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the JwtAuthenticationFilter.
 * These tests focus on the filter's behavior when processing HTTP requests
 * with various JWT token scenarios.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Authentication Filter Unit Tests")
class JwtAuthenticationFilterTest {

  @Mock
  private JwtTokenUtil jwtTokenUtil;

  @Mock
  private UserDetailsService userDetailsService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @InjectMocks
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  private final String authHeader = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.invalid-signature";
  private UserDetails userDetails;

  @BeforeEach
  void setUp() {
    // Given
    SecurityContextHolder.clearContext();
    userDetails = new User("testuser", "password", new ArrayList<>());
  }

  @Test
  @DisplayName("given a valid token, when filtering, then authenticates the user successfully")
  void givenValidToken_whenFiltering_thenAuthenticatesSuccessfully() throws ServletException, IOException {
    // Given
    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtTokenUtil.extractUsername(anyString())).thenReturn("testuser");
    when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
    when(jwtTokenUtil.validateToken(anyString(), any(UserDetails.class))).thenReturn(true);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("given no Authorization header, when filtering, then skips authentication")
  void givenNoHeader_whenFiltering_thenSkipsAuthentication() throws ServletException, IOException {
    // Given
    when(request.getHeader("Authorization")).thenReturn(null);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
    verify(jwtTokenUtil, never()).extractUsername(anyString());
  }

  @Test
  @DisplayName("given a malformed header, when filtering, then throws InvalidTokenException")
  void givenMalformedHeader_whenFiltering_thenThrowsInvalidTokenException() throws ServletException, IOException {
    // Given
    when(request.getHeader("Authorization")).thenReturn("MalformedToken");

    // When & Then
    assertThrows(InvalidTokenException.class, () ->
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain, never()).doFilter(request, response);
    verify(jwtTokenUtil, never()).extractUsername(anyString());
  }

  @Test
  @DisplayName("given an invalid token, when filtering, then throws InvalidTokenException")
  void givenInvalidToken_whenFiltering_thenThrowsInvalidTokenException() throws ServletException, IOException {
    // Given
    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtTokenUtil.extractUsername(anyString())).thenReturn("testuser");
    when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
    when(jwtTokenUtil.validateToken(anyString(), any(UserDetails.class))).thenReturn(false);

    // When & Then
    assertThrows(InvalidTokenException.class, () ->
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain, never()).doFilter(request, response);
  }

  @Test
  @DisplayName("given a valid token but a nonexistent user, when filtering, then skips authentication")
  void givenTokenForNonexistentUser_whenFiltering_thenSkipsAuthentication() throws ServletException, IOException {
    // Given
    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtTokenUtil.extractUsername(anyString())).thenReturn("nonexistentuser");
    when(userDetailsService.loadUserByUsername("nonexistentuser")).thenReturn(null);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }
}
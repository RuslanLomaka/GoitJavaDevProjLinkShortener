package org.decepticons.linkshortener.api.security.jwt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import org.decepticons.linkshortener.api.repository.RevokedTokenRepository;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Authentication Filter Unit Tests")
class JwtAuthenticationFilterTest {

  @Mock
  private JwtTokenUtil jwtTokenUtil;

  @Mock
  private UserDetailsService userDetailsService;

  @Mock
  private RevokedTokenRepository revokedTokenRepository;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @InjectMocks
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  private final String authHeader
      = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.invalid-signature";
  private UserDetails userDetails;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    userDetails = new User("testuser", "password", new ArrayList<>());
  }

  @Test
  @DisplayName("given a valid token, when filtering, then authenticates the user successfully")
  void givenValidToken_whenFiltering_thenAuthenticatesSuccessfully()
      throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtTokenUtil.extractUsername(anyString())).thenReturn("testuser");
    when(revokedTokenRepository.existsByToken(anyString())).thenReturn(false);
    when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
    when(jwtTokenUtil.validateToken(anyString(), any(UserDetails.class))).thenReturn(true);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("given a revoked token, when filtering,skips auth. without calling the filter chain")
  void givenRevokedToken_whenFiltering_thenSkipsAuthentication()
      throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(revokedTokenRepository.existsByToken(anyString())).thenReturn(true);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    // Do not verify filterChain.doFilter because it is intentionally NOT called
    verify(jwtTokenUtil, never()).extractUsername(anyString());
  }

  @Test
  @DisplayName("given no Authorization header, when filtering, then skips authentication")
  void givenNoHeader_whenFiltering_thenSkipsAuthentication()
      throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(null);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
    verify(jwtTokenUtil, never()).extractUsername(anyString());
  }

  @Test
  @DisplayName("given a malformed header, when filtering, then skips authentication")
  void givenMalformedHeader_whenFiltering_thenSkipsAuthentication()
      throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("MalformedToken");

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
    verify(jwtTokenUtil, never()).extractUsername(anyString());
  }

  @Test
  @DisplayName
      ("given an invalid token, when filtering, then skips auth. without calling the filter chain")
  void givenInvalidToken_whenFiltering_thenSkipsAuthentication()
      throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(revokedTokenRepository.existsByToken(anyString())).thenReturn(false);
    when(jwtTokenUtil.extractUsername(anyString())).thenReturn("testuser");
    when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
    when(jwtTokenUtil.validateToken(anyString(), any(UserDetails.class))).thenReturn(false);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    // Do not verify filterChain.doFilter because it is intentionally NOT called
  }

  @Test
  @DisplayName("given a valid token but a nonexistent user, when filtering, then skips auth.")
  void givenTokenForNonexistentUser_whenFiltering_thenSkipsAuthentication()
      throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(authHeader);
    when(revokedTokenRepository.existsByToken(anyString())).thenReturn(false);
    when(jwtTokenUtil.extractUsername(anyString())).thenReturn("nonexistentuser");
    when(userDetailsService.loadUserByUsername("nonexistentuser")).thenReturn(null);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }
}

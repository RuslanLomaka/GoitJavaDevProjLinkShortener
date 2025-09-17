package org.decepticons.linkshortener.api.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.decepticons.linkshortener.api.repository.RevokedTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that intercepts incoming requests and validates JWT tokens.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  /**
   * Logger for the JwtAuthenticationFilter class.
   */
  private static final Logger LOG = LoggerFactory.getLogger(
      JwtAuthenticationFilter.class
  );

  /**
   * The name of the authorization header.
   */
  private static final String AUTHORIZATION_HEADER = "Authorization";

  /**
   * The prefix for a Bearer token.
   */
  private static final String BEARER_PREFIX = "Bearer ";

  /**
   * Utility for handling JWT tokens.
   */
  private final JwtTokenUtil jwtTokenUtil;

  /**
   * Service for loading user details.
   */
  private final UserDetailsService userDetailsService;

  /**
   * Repository to check for revoked tokens.
   */
  private final RevokedTokenRepository revokedTokenRepository;

  /**
   * Constructs a new JwtAuthenticationFilter.
   *
   * @param jwtTokenUtilParam The JWT utility.
   * @param userDetailsServiceParam The user details service.
   * @param revokedTokenRepositoryParam The revoked token repository.
   */
  public JwtAuthenticationFilter(
      final JwtTokenUtil jwtTokenUtilParam,
      final UserDetailsService userDetailsServiceParam,
      final RevokedTokenRepository revokedTokenRepositoryParam
  ) {
    this.jwtTokenUtil = jwtTokenUtilParam;
    this.userDetailsService = userDetailsServiceParam;
    this.revokedTokenRepository = revokedTokenRepositoryParam;
  }

  /**
   * Filters incoming requests to validate JWT tokens and authenticate users.
   *
   * @param request The servlet request.
   * @param response The servlet response.
   * @param filterChain The filter chain.
   * @throws ServletException if a servlet-specific error occurs.
   * @throws IOException if an I/O error occurs.
   */
  @Override
  protected void doFilterInternal(final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

    final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

    if (authorizationHeader == null
        || !authorizationHeader.startsWith(BEARER_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    String jwtToken = authorizationHeader.substring(BEARER_PREFIX.length());

    // Check if the token is revoked
    if (revokedTokenRepository.existsByToken(jwtToken)) {
      LOG.warn("Revoked token used: {}", jwtToken);
      SecurityContextHolder.clearContext();
      response.sendError(
          HttpServletResponse.SC_UNAUTHORIZED,
          "Token has been revoked");
      return;
    }

    final String username = jwtTokenUtil.extractUsername(jwtToken);

    if (username != null
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      if (userDetails == null) {
        LOG.warn("User with username {} not found", username);
        filterChain.doFilter(request, response);
        return;
      }

      if (!jwtTokenUtil.validateToken(jwtToken, userDetails)) {
        SecurityContextHolder.clearContext();
        response.sendError(
            HttpServletResponse.SC_UNAUTHORIZED,
            "Token is expired or invalid");
        return;
      }

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities());
      authToken.setDetails(
          new WebAuthenticationDetailsSource().buildDetails(request)
      );
      SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Determines whether this filter should be applied to the current request.
   *
   * @param request The servlet request.
   * @return true if the filter should not be applied, false otherwise.
   */
  @Override
  protected boolean shouldNotFilter(final HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/api/v1/auth/register")
        || path.startsWith("/api/v1/auth/login")
        || path.startsWith("/api/v1/auth/refresh")
        || path.startsWith("/api/links/")
        || path.startsWith("/health")
        || path.startsWith("/h2-console")
        || path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs");
  }
}

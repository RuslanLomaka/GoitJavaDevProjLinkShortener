package org.decepticons.linkshortener.api.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.decepticons.linkshortener.api.exceptions.InvalidTokenException;
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
  /** Logger for JWT authentication filter. */
  private static final Logger LOG =
      LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  /** HTTP header containing the JWT token. */
  private static final String AUTHORIZATION_HEADER = "Authorization";

  /** Prefix used in the Authorization header. */
  private static final String BEARER_PREFIX = "Bearer ";

  /** Utility class for JWT operations. */
  private final JwtTokenUtil jwtTokenUtil;

  /** Service to load user details from the database. */
  private final UserDetailsService userDetailsService;

  /**
   * Constructs a JwtAuthenticationFilter with required dependencies.
   *
   * @param inJwtTokenUtil utility to parse and validate JWT tokens
   * @param inUserDetailsService service to load user details
   */
  public JwtAuthenticationFilter(final JwtTokenUtil inJwtTokenUtil,
      final UserDetailsService inUserDetailsService) {
    this.jwtTokenUtil = inJwtTokenUtil;
    this.userDetailsService = inUserDetailsService;
  }

  /**
   * Filters each request to validate JWT tokens and set authentication.
   *
   * @param request the HTTP request
   * @param response the HTTP response
   * @param filterChain the filter chain
   * @throws ServletException if a servlet error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

    final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
    String jwtToken;

    if (authorizationHeader == null) {
      LOG.debug("No JWT token, skipping authentication");
      filterChain.doFilter(request, response);
      return;
    }

    if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
      LOG.warn("JWT Token does not begin with Bearer String");
      throw new InvalidTokenException("JWT Token must start with 'Bearer '");
    }

    jwtToken = authorizationHeader.substring(BEARER_PREFIX.length());

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
        throw new InvalidTokenException("Token is expired or invalid");
      }

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(userDetails, null,
              userDetails.getAuthorities());
      authToken.setDetails(
          new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Determines if this filter should not apply to a given request.
   *
   * @param request the HTTP request
   * @return true if filter should be skipped, false otherwise
   */
  @Override
  protected boolean shouldNotFilter(final HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/api/v1/auth/register")
        || path.startsWith("/api/v1/auth/login")
        || path.startsWith("/api/v1/auth/refresh")
        || path.startsWith("/api/v1/links/")
        || path.startsWith("/health")
        || path.startsWith("/h2-console")
        || path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs");
  }

}

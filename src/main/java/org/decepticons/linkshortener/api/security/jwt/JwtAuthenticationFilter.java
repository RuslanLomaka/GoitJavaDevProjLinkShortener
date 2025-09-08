package org.decepticons.linkshortener.api.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
   * @param tokenUtil utility to parse and validate JWT tokens
   * @param detailsService service to load user details
   */
  public JwtAuthenticationFilter(
      final JwtTokenUtil tokenUtil,
      final UserDetailsService detailsService) {
    this.jwtTokenUtil = tokenUtil;
    this.userDetailsService = detailsService;
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
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain) throws ServletException, IOException {

    final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
    String username = null;
    String jwtToken = null;

    if (authorizationHeader != null
        && authorizationHeader.startsWith(BEARER_PREFIX)) {
      jwtToken = authorizationHeader.substring(BEARER_PREFIX.length());
      try {
        username = jwtTokenUtil.extractUsername(jwtToken);
      } catch (ExpiredJwtException e) {
        LOG.warn("JWT Token has expired: {}", e.getMessage());
        response.sendError(
            HttpServletResponse.SC_UNAUTHORIZED,
            "JWT Token has expired"
        );
        return;
      } catch (Exception e) {
        LOG.warn("Unable to parse JWT Token: {}", e.getMessage());
        response.sendError(
            HttpServletResponse.SC_UNAUTHORIZED,
            "Invalid JWT Token"
        );
        return;
      }
    } else {
      if (authorizationHeader != null) {
        LOG.warn("JWT Token does not begin with Bearer String");
      } else {
        LOG.debug("No JWT token, skipping authentication");
        filterChain.doFilter(request, response);
        return;
      }
      response.sendError(
          HttpServletResponse.SC_UNAUTHORIZED,
          "JWT Token is missing or invalid"
      );
      return;
    }

    if (username != null
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      } else {
        response.sendError(
            HttpServletResponse.SC_UNAUTHORIZED,
            "JWT Token is invalid"
        );
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Determines if this filter should not apply to a given request.
   *
   * @param req the HTTP request
   * @return true if filter should be skipped, false otherwise
   */
  @Override
  protected boolean shouldNotFilter(final HttpServletRequest req) {
    String p = req.getRequestURI();
    return p.startsWith("/api/v1/health")
        || p.startsWith("/swagger-ui/")
        || p.startsWith("/v3/api-docs/")
        || p.startsWith("/h2-console");
  }
}

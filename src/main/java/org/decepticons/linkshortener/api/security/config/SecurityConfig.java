/**
 * Security configuration package for handling JWT
 * authentication and authorization.
 */

package org.decepticons.linkshortener.api.security.config;

import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.decepticons.linkshortener.api.security.jwt.JwtAuthenticationFilter;
import org.decepticons.linkshortener.api.security.model.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the application.
 * Sets up JWT authentication, authorization rules and user details service.
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /**
   * Configures the Spring Security filter chain.
   *
   * @param http the HttpSecurity configuration
   * @param jwtAuthenticationFilter custom JWT filter
   * @return the configured SecurityFilterChain
   * @throws Exception if a configuration error occurs
   */
  @Bean
  public SecurityFilterChain securityFilterChain(
      final HttpSecurity http,
      final JwtAuthenticationFilter jwtAuthenticationFilter
  ) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(

                // API endpoints
                "/api/v1/auth/register",
                "/api/v1/auth/login",
                "/api/v1/auth/refresh",

                // Redirect endpoint
                "/api/links/**",

                // Documentation & health endpoints
                "/api/v1/cache",
                "/health",
                "/h2-console/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs",
                "/v3/api-docs/**"
                )
            .permitAll()
            .requestMatchers(
                HttpMethod.GET,
                "/api/v1/urls"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .headers(headers -> headers.frameOptions(
            HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

    http.addFilterBefore(
        jwtAuthenticationFilter,
        UsernamePasswordAuthenticationFilter.class
    );

    return http.build();
  }

  /**
   * Provides a BCrypt password encoder.
   *
   * @return password encoder bean
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Provides a UserDetailsService that loads users from the repository.
   *
   * @param userRepository repository to access user data
   * @return UserDetailsService bean
   */

  @Bean
  public UserDetailsService userDetailsService(
      final UserRepository userRepository
  ) {
    return username -> {
      User user = userRepository.findByUsername(username)
          .orElseThrow(() -> new UsernameNotFoundException(
              "User was not found"
          ));

      return new CustomUserDetails(user);
    };
  }

  /**
   * Provides the {@link AuthenticationManager} bean.
   *
   * @param authenticationConfiguration authentication configuration
   * @return the {@link AuthenticationManager} bean
   * @throws Exception if the manager cannot be retrieved
   */
  @Bean
  public AuthenticationManager authenticationManager(
      final AuthenticationConfiguration authenticationConfiguration
  ) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}

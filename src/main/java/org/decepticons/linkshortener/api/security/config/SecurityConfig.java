/**
 * Security configuration package for handling JWT
 * authentication and authorization.
 */

package org.decepticons.linkshortener.api.security.config;

import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.decepticons.linkshortener.api.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
            .requestMatchers("/auth/register",
                "/auth/login",
                "/api/v1/health",
                "/swagger-ui/**",
                "/v3/api-docs/**",
              "/h2-console/**")        // ADD: allow H2 console)
            .permitAll()
            .anyRequest().authenticated()
        )
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
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

      return org.springframework.security.core.userdetails.User
          .withUsername(user.getUsername())
          .password(user.getPasswordHash())
          .build();
    };
  }
}

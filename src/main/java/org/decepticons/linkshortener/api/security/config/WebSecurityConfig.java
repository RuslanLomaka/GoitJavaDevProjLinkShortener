package org.decepticons.linkshortener.api.security.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;


/**
 * Security configuration for exposing the {@link AuthenticationManager} bean.
 */
@Configuration
public class WebSecurityConfig {

  /**
   * Provides the {@link AuthenticationManager} bean used for authentication.
   *
   * @param authenticationConfiguration configuration that supplies the manager
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

package org.decepticons.linkshortener.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI (Swagger) documentation.
 * Defines API groups and paths to be exposed.
 */
@Configuration
public class OpenApiConfig {
  /**
   * Defines a GroupedOpenApi bean for version 1 of the API.
   * Only paths matching "/api/**" will be included in this group.
   *
   * @return a configured GroupedOpenApi instance
   */
  @Bean
  public GroupedOpenApi apiGroup() {
    return GroupedOpenApi.builder()
        .group("api")
        .pathsToMatch("/api/**", "/auth/**", "/health/**")
        .build();
  }
}

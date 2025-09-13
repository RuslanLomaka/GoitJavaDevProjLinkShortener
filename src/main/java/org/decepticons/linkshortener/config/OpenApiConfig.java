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
   * Includes only versioned paths ("/api/v1/**").
   */
  @Bean
  public GroupedOpenApi apiV1Group() {
    return GroupedOpenApi.builder()
        .group("api-v1")
        .packagesToScan("org.decepticons.linkshortener.api.v1.controller")
        .pathsToMatch("/api/v1/**")
        .build();
  }

  /**
   * Defines a separate group for public (unversioned) endpoints
   * like redirect and health.
   */
  @Bean
  public GroupedOpenApi publicGroup() {
    return GroupedOpenApi.builder()
        .group("public")                 // label in Swagger dropdown
        .pathsToMatch("/api/links/**", "/health")
        .build();
  }
}

package org.decepticons.linkshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Link Shortener application.
 * This class bootstraps the Spring Boot application by calling
 * {@link SpringApplication#run(Class, String...)}.
 * </p>
 */
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class LinkShortenerApplication {

  /**
   * Main method that starts the Link Shortener application.
   *
   * @param args command-line arguments passed during application startup
   */
  public static void main(String[] args) {
    SpringApplication.run(LinkShortenerApplication.class, args);
  }
}

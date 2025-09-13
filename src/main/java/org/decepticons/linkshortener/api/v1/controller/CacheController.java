package org.decepticons.linkshortener.api.v1.controller;

import org.decepticons.linkshortener.api.service.CacheInspectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for cache inspection.
 * Provides an endpoint to inspect the contents of the short links cache.
 * This is primarily for debugging and monitoring purposes.
 * Note: In a production environment, access to this endpoint should be
 * restricted to authorized personnel only, as it may expose sensitive data.
 */
@RestController
@RequestMapping("/api/v1/cache")
public class CacheController {

  private final CacheInspectionService cacheInspectionService;

  /**
   * Constructs a new {@link CacheController} with the given dependencies.
   *
   * @param cacheInspectionService the service responsible for cache inspection
   */
  public CacheController(CacheInspectionService cacheInspectionService) {
    this.cacheInspectionService = cacheInspectionService;
  }

  /**
   * Endpoint to inspect the contents of the short links cache.
   * This is primarily for debugging and monitoring purposes.
   * Note: In a production environment, access to this endpoint should be
   * restricted to authorized personnel only, as it may expose sensitive data.
   */
  @GetMapping
  public void inspectCache() {
    cacheInspectionService.printCache("shortLinksCache");
  }
}

package org.decepticons.linkshortener.api.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * Service for inspecting and printing cache contents.
 */
@Service
public class CacheInspectionService {


  private final CacheManager cacheManager;
  private final Logger logger = LoggerFactory.getLogger(CacheInspectionService.class);

  /**
   * Constructs a CacheInspectionService with the given CacheManager.
   *
   * @param cacheManager the CacheManager to use for accessing caches
   */

  public CacheInspectionService(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  /**
   * Prints the contents of the specified cache to the console.
   *
   * @param cacheName the name of the cache to inspect
   */
  public void printCache(String cacheName) {
    Cache cache = cacheManager.getCache(cacheName);
    if (cache != null) {
      logger.debug("Cache Name: {}", cacheName);
      logger.debug("Cache Content: {}", cache.getNativeCache());
    } else {
      logger.debug("Cache with name {} not found", cacheName);
    }
  }

}

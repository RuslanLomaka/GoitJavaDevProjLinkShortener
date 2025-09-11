package org.decepticons.linkshortener.api.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * Service for inspecting and printing cache contents.
 */
@Service
public class CacheInspectionService {

  @Autowired
  private CacheManager cacheManager;


  /**
   * Prints the contents of the specified cache to the console.
   *
   * @param cacheName the name of the cache to inspect
   */
  public void printCache(String cacheName) {
    Cache cache = cacheManager.getCache(cacheName);
    if (cache != null) {
      System.out.println("Cache Name: " + cacheName);
      System.out.println("Cache Content: " + cache.getNativeCache().toString());
    } else {
      System.out.println("Cache with name " + cacheName + " not found.");
    }
  }

}

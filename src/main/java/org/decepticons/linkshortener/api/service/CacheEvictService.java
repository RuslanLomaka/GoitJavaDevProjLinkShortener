package org.decepticons.linkshortener.api.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * Service for evicting cache entries related to short links.
 */
@Service
public class CacheEvictService {
  /**
   * Evicts the cache entry for the given short link code.
   *
   * @param code the short link code whose cache entry should be evicted
   */
  @CacheEvict(value = "shortLinksCache", key = "#code")
  public void evictLink(String code) { // it's indeed should be empty, don't delete it :)
  }
}

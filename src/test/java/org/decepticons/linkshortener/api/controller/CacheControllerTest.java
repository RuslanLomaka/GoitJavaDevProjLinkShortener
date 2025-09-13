package org.decepticons.linkshortener.api.controller;

import org.decepticons.linkshortener.api.service.CacheInspectionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class CacheControllerTest {
  @Mock
  private CacheInspectionService cacheInspectionService;

  @InjectMocks
  private CacheController cacheController;

  @Test
  @DisplayName("Inspect Cache - Service Called")
  void inspectCache_SUCCESS() {

    cacheController.inspectCache();

    verify(cacheInspectionService, times(1)).printCache("shortLinksCache");
  }
}

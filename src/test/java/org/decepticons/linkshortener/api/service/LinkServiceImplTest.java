package org.decepticons.linkshortener.api.service;

import org.decepticons.linkshortener.api.dto.LinkResponseDto;
import org.decepticons.linkshortener.api.dto.UrlRequestDto;
import org.decepticons.linkshortener.api.model.Link;
import org.decepticons.linkshortener.api.model.LinkStatus;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.LinkRepository;
import org.decepticons.linkshortener.api.service.impl.LinkServiceImpl;
import org.decepticons.linkshortener.api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class LinkServiceImplTest {

  @Mock
  private LinkRepository linkRepository;

  @Mock
  private UserServiceImpl userServiceImpl;

  @InjectMocks
  private LinkServiceImpl linkServiceImpl;







  @Test
  @DisplayName("Link Creation - Success")
  void testLinkCreation_Success() {
    UrlRequestDto urlRequestDto = new UrlRequestDto();
    User user = new User();
//    user.setId(UUID.randomUUID());


    urlRequestDto.setUrl("https://www.example.com");

    when(userServiceImpl.getCurrentUser()).thenReturn(user);
    when(linkRepository.save(any(Link.class))).thenAnswer(i -> i.getArguments()[0]);

    LinkResponseDto result = linkServiceImpl.createLink(urlRequestDto);

    assertEquals(urlRequestDto.getUrl(), result.originalUrl());
    assertEquals(user.getId(), result.ownerId());
    assertNotNull(result.code());
    assertEquals("ACTIVE", result.status());
  }

  @Test
  @DisplayName("Increment of Clicks - Success")
  void incrementOfClicks_Success() {
    LinkResponseDto linkResponseDto = new LinkResponseDto(
        UUID.randomUUID(),
        "abc123",
        "https://www.example.com",
        Instant.now(),
        Instant.now().plusSeconds(86400),
        0,
        "ACTIVE",
        UUID.randomUUID()
    );
    Link link = new Link();
    link.setOwner(new User());
    link.setCode("abc123");
    link.setClicks(0);

    when(linkRepository.incrementClicksByCodeNative("abc123")).thenReturn(1);
    when(linkRepository.findByCode("abc123")).thenReturn(Optional.of(link));
    LinkResponseDto result = linkServiceImpl.incrementClicks(linkResponseDto);
    assertNotEquals(1, result.clicks());

  }

  @Test
  @DisplayName("Mapping to Response DTO - Success")
  void testMapToResponse_Success() {
    UUID userId = UUID.randomUUID();
    User owner = new User();
//    owner.setId(userId);

    Link link = new Link();
    link.setCode("abc123");
    link.setOriginalUrl("https://example.com");
    link.setClicks(5);
    link.setStatus(LinkStatus.ACTIVE);
    link.setExpiresAt(Instant.now().plus(2, ChronoUnit.DAYS));
    link.setOwner(owner);

    LinkResponseDto dto = linkServiceImpl.mapToResponse(link);

    assertEquals(link.getCode(), dto.code());
    assertEquals(link.getOriginalUrl(), dto.originalUrl());
    assertEquals(link.getClicks(), dto.clicks());
    assertEquals(link.getStatus().name(), dto.status());
    assertEquals(owner.getId(), dto.ownerId());
    assertEquals(link.getExpiresAt(), dto.expiresAt());

    assertNull(dto.id());
  }

  @Test
  @DisplayName("Get Link By Code - Success")
  void getLinkByCode_Success() {

    User owner = new User();
//    owner.setId(UUID.randomUUID());

    Link link = new Link();
    link.setOwner(owner);
    link.setCode("abc123");
    link.setOriginalUrl("https://example.com");
    link.setClicks(5);
    link.setStatus(LinkStatus.ACTIVE);
    link.setExpiresAt(Instant.now().plus(2, ChronoUnit.DAYS));


    when(linkRepository.findByCode("abc123")).thenReturn(Optional.of(link));

    LinkResponseDto result = linkServiceImpl.getLinkByCode("abc123");

    assertEquals("abc123", result.code());
    assertEquals("https://example.com", result.originalUrl());
    assertEquals(owner.getId(), result.ownerId());
    assertEquals(5, result.clicks());
    assertEquals(LinkStatus.ACTIVE.name(), result.status());

  }


  @Test
  @DisplayName("Deactivate Link - Success")
  void deactivateLink_Success() {

    User owner = new User();
    LinkResponseDto linkResponseDto = new LinkResponseDto(
        UUID.randomUUID(),
        "abc123",
        "https://www.example.com",
        Instant.now(),
        Instant.now().plusSeconds(86400),
        0,
        "ACTIVE",
        UUID.randomUUID()
    );

    Link link = new Link();
    link.setOwner(owner);
    link.setCode("abc123");
    link.setStatus(LinkStatus.ACTIVE);


    when(linkRepository.findByCode("abc123")).thenReturn(Optional.of(link));
    when(linkRepository.save(any(Link.class))).thenAnswer(i -> i.getArguments()[0]);

    LinkResponseDto result = linkServiceImpl.deactivateLink(linkResponseDto);

    assertEquals("INACTIVE", result.status());
  }


  @Test
  @DisplayName("Validate Link - Success")
  void validateLink_Success() {
    LinkResponseDto linkResponseDto = new LinkResponseDto(
        UUID.randomUUID(),
        "abc123",
        "https://www.example.com",
        Instant.now(),
        Instant.now().plusSeconds(86400),
        0,
        "ACTIVE",
        UUID.randomUUID()
    );

    linkServiceImpl.validateLink(linkResponseDto);
    Assertions.assertTrue(linkServiceImpl.validateLink(linkResponseDto));

  }

  @Test
  @DisplayName("Update Link Expiration - Success")
  void testUpdateLinkExpiration(){
    String code = "abc123";
    User owner = new User();
    owner.setUsername("testUser");
//    owner.setId(UUID.randomUUID());

    Link link = new Link();
    link.setCode(code);
    link.setExpiresAt(Instant.now().plusSeconds(3600));
    link.setStatus(LinkStatus.ACTIVE);
    link.setOwner(owner);

    Authentication auth = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);

    when(linkRepository.findByCode(code)).thenReturn(Optional.of(link));
    when(linkRepository.save(any(Link.class))).thenAnswer(i -> i.getArguments()[0]);
    when(userServiceImpl.getCurrentUserId()).thenReturn(owner.getId());

    LinkResponseDto response = linkServiceImpl.updateLinkExpiration(code, Instant
        .now()
        .plusSeconds(7200));

    assertNotNull(response);
    assertEquals(code, response.code());
    assertEquals(link.getExpiresAt(), response.expiresAt());
    verify(linkRepository, times(1)).findByCode(code);

  }
}

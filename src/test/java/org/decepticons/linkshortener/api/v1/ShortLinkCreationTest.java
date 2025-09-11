package org.decepticons.linkshortener.api.v1;


import org.decepticons.linkshortener.api.controller.LinkController;
import org.decepticons.linkshortener.api.dto.LinkResponseDto;
import org.decepticons.linkshortener.api.dto.UrlRequestDto;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.decepticons.linkshortener.api.service.LinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@ExtendWith(MockitoExtension.class)
class ShortLinkCreationTest {

  @Mock
  private LinkService linkService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private LinkController linkController;


  @BeforeEach
  void setUp() {

    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken("someName", null, null)
    );

  }

  @Test
  void testCreateShortLink_Success() {
    UrlRequestDto urlRequestDto = new UrlRequestDto();
    urlRequestDto.setUrl("https://example.com/some/long/url");

    User fakeUser = new User();
    fakeUser.setId(UUID.randomUUID());
    fakeUser.setUsername("someName");


    LinkResponseDto fakeResponse = new LinkResponseDto(
        UUID.randomUUID(),
        "abc123",
        urlRequestDto.getUrl(),
        Instant.now(),
        Instant.now().plus(2, ChronoUnit.DAYS),
        0,
        "ACTIVE",
        fakeUser.getId()
    );


    Mockito.when(userRepository.findByUsername("someName")).thenReturn(java.util.Optional.of(fakeUser));
    Mockito.when(linkService.createLink(urlRequestDto, fakeUser)).thenReturn(fakeResponse);

    ResponseEntity<LinkResponseDto> response = linkController.createLink(urlRequestDto);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("abc123", response.getBody().code());
    assertEquals("https://example.com/some/long/url", response.getBody().originalUrl());


    Mockito.verify(userRepository).findByUsername("someName");
    Mockito.verify(linkService).createLink(urlRequestDto, fakeUser);

  }


  @Test
  void testCreateShortLink_UserNotFound() {
    UrlRequestDto urlRequestDto = new UrlRequestDto();
    urlRequestDto.setUrl("https://example.com/some/long/url");

    Mockito.when(userRepository.findByUsername("someName")).thenReturn(java.util.Optional.empty());

    try {
      linkController.createLink(urlRequestDto);
    } catch (Exception e) {
      assertEquals("No such user found in the system: someName", e.getMessage());
    }

    Mockito.verify(userRepository).findByUsername("someName");
    Mockito.verify(linkService, Mockito.never()).createLink(Mockito.any(), Mockito.any());
  }
}

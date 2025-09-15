package org.decepticons.linkshortener.api.controller;


import org.decepticons.linkshortener.api.dto.LinkResponseDto;
import org.decepticons.linkshortener.api.dto.UpdateLinkExpirationRequestDto;
import org.decepticons.linkshortener.api.dto.UrlRequestDto;
import org.decepticons.linkshortener.api.exception.NoSuchUserFoundInTheSystemException;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.service.LinkService;
import org.decepticons.linkshortener.api.service.impl.UserServiceImpl;
import org.decepticons.linkshortener.api.v1.controller.LinkCrudController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Short Link Creation Tests")
class LinkCrudControllerTest {

  @Mock
  private LinkService linkService;

  @Mock
  UserServiceImpl userServiceImpl;


  @InjectMocks
  private LinkCrudController linkController;


  @BeforeEach
  void setUp() {

    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken("someName", null, null)
    );

  }

  @Test
  @DisplayName("Create Short Link - Success")
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


    when(linkService.createLink(urlRequestDto)).thenReturn(fakeResponse);


    ResponseEntity<LinkResponseDto> response = linkController.createLink(urlRequestDto);


    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("abc123", response.getBody().code());
    assertEquals("https://example.com/some/long/url", response.getBody().originalUrl());



    verify(linkService).createLink(urlRequestDto);

  }


  @Test
  @DisplayName("Create Short Link - User Not Found")
  void testCreateShortLink_UserNotFound_Controller() {
    UrlRequestDto urlRequestDto = new UrlRequestDto();
    urlRequestDto.setUrl("https://example.com/some/long/url");


    when(linkService.createLink(Mockito.any(UrlRequestDto.class)))
        .thenThrow(new NoSuchUserFoundInTheSystemException("User not found", "some-user-id"));

    NoSuchUserFoundInTheSystemException ex = assertThrows(
        NoSuchUserFoundInTheSystemException.class,
        () -> linkController.createLink(urlRequestDto)
    );

    assertEquals("User not found", ex.getMessage());

    verify(linkService).createLink(urlRequestDto);
  }



  @Test
  @DisplayName("Get All My Links - Success")
  void getAllMyLinks_SUCCESS() {
    int page = 0;
    int size = 10;

    LinkResponseDto link1 = new LinkResponseDto(
        UUID.randomUUID(),
        "code1",
        "https://example.com/1",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        0,
        "ACTIVE",
        UUID.randomUUID()
    );

    LinkResponseDto link2 = new LinkResponseDto(
        UUID.randomUUID(),
        "code2",
        "https://example.com/2",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        0,
        "ACTIVE",
        UUID.randomUUID()
    );

    Page<LinkResponseDto> mockPage = new PageImpl<>(List.of(link1, link2));

    when(linkService.getAllMyLinks(page, size)).thenReturn(mockPage);

    ResponseEntity<Page<LinkResponseDto>> response = linkController.getAllMyLinks(page, size);

    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().getContent().size());
    assertEquals("code1", response.getBody().getContent().get(0).code());
    assertEquals("code2", response.getBody().getContent().get(1).code());


    verify(linkService, times(1)).getAllMyLinks(page, size);

  }

  @Test
  @DisplayName("Get All My Active Links - Success")
  void getAllMyActiveLinks_SUCCESS() {
    int page = 0;
    int size = 10;


    LinkResponseDto link1 = new LinkResponseDto(
        UUID.randomUUID(),
        "code1",
        "https://example.com/1",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        0,
        "ACTIVE",
        UUID.randomUUID()
    );

    LinkResponseDto link2 = new LinkResponseDto(
        UUID.randomUUID(),
        "code2",
        "https://example.com/2",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        0,
        "ACTIVE",
        UUID.randomUUID()
    );

    Page<LinkResponseDto> mockPage = new PageImpl<>(List.of(link1, link2));


    when(linkService.getAllMyActiveLinks(page, size)).thenReturn(mockPage);


    ResponseEntity<Page<LinkResponseDto>> response = linkController.getAllMyActiveLinks(page, size);


    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().getContent().size());
    assertEquals("code1", response.getBody().getContent().get(0).code());
    assertEquals("code2", response.getBody().getContent().get(1).code());


    verify(linkService, times(1)).getAllMyActiveLinks(page, size);
  }



  @Test
  @DisplayName("Delete Link - Success")
  void testDeleteLink_SUCCESS() {

    UUID linkId = UUID.randomUUID();
    String mockCode = "abc123";

    ResponseEntity<Void> mockResponse = ResponseEntity.noContent().build();

    when(linkService.deleteLink(linkId)).thenReturn(mockCode);

    ResponseEntity<Void> response = linkController.deleteLink(linkId);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());

    verify(linkService, times(1)).deleteLink(linkId);
  }

  @Test
  @DisplayName("Update Link Expiration Date - Success")
  void testUpdateLinkExpirationDate(){

    String code = "abc123";

    UpdateLinkExpirationRequestDto requestDto
        = new UpdateLinkExpirationRequestDto(Instant.now().plus(5, ChronoUnit.DAYS));


    when(linkService.updateLinkExpiration(code, requestDto.getNewExpirationDate()))
        .thenReturn(new LinkResponseDto(
            UUID.randomUUID(),
            code,
            "https://example.com/some/long/url",
            Instant.now(),
            requestDto.getNewExpirationDate(),
            0,
            "ACTIVE",
            UUID.randomUUID()
        ));

    ResponseEntity<LinkResponseDto> response = linkController.updateLinkExpiration(requestDto, code);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(code, response.getBody().code());
    verify(linkService, times(1))
        .updateLinkExpiration(code, requestDto.getNewExpirationDate());
  }
}

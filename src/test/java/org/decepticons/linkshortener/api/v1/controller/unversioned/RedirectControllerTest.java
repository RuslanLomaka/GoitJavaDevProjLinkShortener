package org.decepticons.linkshortener.api.v1.controller.unversioned;


import jakarta.servlet.http.HttpServletResponse;
import org.decepticons.linkshortener.api.dto.LinkResponseDto;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.service.impl.LinkServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RedirectControllerTest {

  @InjectMocks
  private RedirectController redirectController;
  @Mock
  private LinkServiceImpl linkServiceImpl;
  @Mock
  private HttpServletResponse httpServletResponse;

  @Test
  void verifyRedirectToOriginalUrl_SUCCESS() throws IOException {
    String code = "abc123";
    User owner = new User();
//    owner.setId(java.util.UUID.randomUUID());

    LinkResponseDto responseDto = new LinkResponseDto(
        java.util.UUID.randomUUID(),
        code,
        "https://www.example.com",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        0,
        "ACTIVE",
        owner.getId()
    );


    when(linkServiceImpl.getLinkByCode(code)).thenReturn(responseDto);
    when(linkServiceImpl.incrementClicks(responseDto)).thenReturn(responseDto);
    when(linkServiceImpl.validateLink(responseDto)).thenReturn(true);
    doNothing().when(httpServletResponse).sendRedirect(responseDto.originalUrl());


    redirectController.redirect(code, httpServletResponse);

    verify(httpServletResponse, times(1)).sendRedirect("https://www.example.com");
    verify(linkServiceImpl, times(1)).incrementClicks(responseDto);
  }

  @Test
  void verifyExceptionThrownWhenLinkNotValid_SUCCESS() throws IOException {
    String code = "abc123";
    User owner = new User();
//    owner.setId(java.util.UUID.randomUUID());

    LinkResponseDto responseDto = new LinkResponseDto(
        java.util.UUID.randomUUID(),
        code,
        "https://www.example.com",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        0,
        "ACTIVE",
        owner.getId()
    );

    when(linkServiceImpl.getLinkByCode(code)).thenReturn(responseDto);
    when(linkServiceImpl.validateLink(responseDto)).thenReturn(false );

    Exception ex = null;


    try {
      redirectController.redirect(code, httpServletResponse);
    }catch (Exception e) {
      ex = e;
    }

    verify(httpServletResponse, never()).sendRedirect("https://www.example.com");
    assertInstanceOf(org.decepticons.linkshortener.api.exception.ShortLinkIsOutOfDateException.class, ex);
  }
}

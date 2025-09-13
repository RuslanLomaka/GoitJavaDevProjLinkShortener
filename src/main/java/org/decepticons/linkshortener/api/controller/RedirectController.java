package org.decepticons.linkshortener.api.controller;


import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.decepticons.linkshortener.api.dto.LinkResponseDto;
import org.decepticons.linkshortener.api.exception.ShortLinkIsOutOfDateException;
import org.decepticons.linkshortener.api.model.LinkStatus;
import org.decepticons.linkshortener.api.service.LinkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller for redirecting short links.
 * <p>
 * Provides an endpoint to redirect to the original URL based on the short code.
 */
@RestController
@RequestMapping("/api/links")
public class RedirectController {

  private final LinkService linkService;


  /**
   * Constructs a new {@link RedirectController} with the given dependencies.
   *
   * @param linkService the service responsible for link business logic
   */
  public RedirectController(LinkService linkService) {
    this.linkService = linkService;
  }


  /**
   * Redirects to the original URL by the given short code.
   *
   * @param code the short URL code
   */
  @GetMapping("/{code}")
  public void redirect(@PathVariable String code, HttpServletResponse response) throws IOException {


    LinkResponseDto linkByCode = linkService.getLinkByCode(code);

    if (linkService.validateLink(linkByCode)) {
      linkService.incrementClicks(linkByCode);
    } else {
      if (linkByCode.status().equalsIgnoreCase(LinkStatus.ACTIVE.name())) {
        linkService.deactivateLink(linkByCode);
      }
      throw new ShortLinkIsOutOfDateException(
          "Short link is out of date: " + code,
          code,
          linkByCode.expiresAt()
      );
    }

    response.sendRedirect(linkByCode.originalUrl());
  }
}

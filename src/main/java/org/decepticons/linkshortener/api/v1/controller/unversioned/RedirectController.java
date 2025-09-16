package org.decepticons.linkshortener.api.v1.controller.unversioned;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.decepticons.linkshortener.api.dto.LinkResponseDto;
import org.decepticons.linkshortener.api.exception.ShortLinkIsOutOfDateException;
import org.decepticons.linkshortener.api.model.LinkStatus;
import org.decepticons.linkshortener.api.service.impl.LinkServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for redirecting short links.
 * Provides an endpoint to redirect to the original URL based on the short code.
 */
@RestController
@RequestMapping("/api/links")
public class RedirectController {

  private final LinkServiceImpl linkServiceImpl;

  /**
   * Constructs a new {@link RedirectController} with the given dependencies.
   *
   * @param linkServiceImpl the service responsible for link business logic
   */
  public RedirectController(LinkServiceImpl linkServiceImpl) {
    this.linkServiceImpl = linkServiceImpl;
  }

  /**
   * Redirects to the original URL by the given short code.
   *
   * @param code the short URL code
   */
  @GetMapping("/{code}")
  public void redirect(@PathVariable String code, HttpServletResponse response) throws IOException {

    LinkResponseDto linkByCode = linkServiceImpl.getLinkByCode(code);

    if (linkServiceImpl.validateLink(linkByCode)) {
      linkByCode = linkServiceImpl.incrementClicks(linkByCode);
    } else {
      if (linkByCode.status().equalsIgnoreCase(LinkStatus.ACTIVE.name())) {
        linkServiceImpl.deactivateLink(linkByCode);
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

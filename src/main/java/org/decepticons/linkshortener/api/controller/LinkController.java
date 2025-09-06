package org.decepticons.linkshortener.api.controller;

import org.decepticons.linkshortener.api.dto.LinkResponse;
import org.decepticons.linkshortener.api.model.Link;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.LinkRepository;
import org.decepticons.linkshortener.api.service.LinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/api/links")
public class LinkController {

    private final LinkService linkService;
    private final LinkRepository linkRepository;

    public LinkController(LinkService linkService, LinkRepository linkRepository) {
        this.linkService = linkService;
        this.linkRepository = linkRepository;
    }

    @PostMapping
    public ResponseEntity<LinkResponse> createLink(
            @RequestParam String originalUrl,
            @RequestParam String username,
            @RequestParam(required = false) Instant expiresAt
    ) {

        User owner = new User();
        owner.setUsername(username);

        LinkResponse response = linkService.createLink(originalUrl, owner, expiresAt);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{code}")
    public ResponseEntity<String> redirect(@PathVariable String code) {
        Optional<Link> optionalLink = linkRepository.findByCode(code);
        if (optionalLink.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Link link = optionalLink.get();

        if (!linkService.isLinkActive(link)) {
            return ResponseEntity.status(410).body("Link expired or inactive");
        }

        linkService.incrementClicks(link);
        return ResponseEntity.status(302)
                .header("Location", link.getOriginalUrl())
                .build();
    }
}

package org.decepticons.linkshortener.api.service;

import org.decepticons.linkshortener.api.dto.LinkResponse;
import org.decepticons.linkshortener.api.model.Link;
import org.decepticons.linkshortener.api.model.LinkStatus;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.LinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Random;

@Service
public class LinkService {

    private final LinkRepository linkRepository;
    private final Random random = new Random(); // для генерації коду

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Transactional
    public LinkResponse createLink(String originalUrl, User owner, Instant expiresAt) {
        Link link = new Link();
        link.setOriginalUrl(originalUrl);
        link.setOwner(owner);
        link.setCode(generateRandomCode());
        link.setExpiresAt(expiresAt); // кінцевий термін дії
        link.setClicks(0L);
        link.setStatus(LinkStatus.ACTIVE);

        linkRepository.save(link);

        return mapToResponse(link);
    }

    @Transactional
    public void incrementClicks(Link link) {
        link.setClicks(link.getClicks() + 1);
        link.setLastAccessedAt(Instant.now());
        linkRepository.save(link);
    }

    public LinkResponse mapToResponse(Link link) {
        return new LinkResponse(
                link.getId(),
                link.getCode(),
                link.getOriginalUrl(),
                link.getCreatedAt(),
                link.getExpiresAt(),
                link.getClicks(),
                link.getStatus().name(),
                link.getOwner().getId()
        );
    }

    private String generateRandomCode() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int idx = random.nextInt(chars.length()); // без Math.random()
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }

    public boolean isLinkActive(Link link) {
        return link.getStatus()

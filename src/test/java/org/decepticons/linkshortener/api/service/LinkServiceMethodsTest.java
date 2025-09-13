package org.decepticons.linkshortener.api.service;

import org.decepticons.linkshortener.api.dto.LinkResponseDto;
import org.decepticons.linkshortener.api.model.Link;
import org.decepticons.linkshortener.api.model.LinkStatus;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.LinkRepository;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;



@ExtendWith(MockitoExtension.class)
public class LinkServiceMethodsTest {
  @Mock
  private LinkRepository linkRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CacheEvictService cacheEvictService;

  @InjectMocks
  private LinkService linkService;

  private final UUID testUserId = UUID.randomUUID();
  private final String testUsername = "testuser";

  @BeforeEach
  void setUpSecurityContext() {

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(testUsername, null, List.of());
    SecurityContextHolder.getContext().setAuthentication(auth);


    User testUser = new User();
    testUser.setId(testUserId);
    testUser.setUsername(testUsername);
    when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Get All My Links - Success")
  void getAllMyLinks_SUCCESS() {
    Link link1 = new Link();
    link1.setOwner(userRepository.findByUsername(testUsername).get());

    Page<Link> mockPage = new PageImpl<>(List.of(link1));

    when(linkRepository.findAllByOwnerId(eq(testUserId), any(Pageable.class)))
        .thenReturn(mockPage);

    Page<LinkResponseDto> result = linkService.getAllMyLinks(0, 10);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(link1.getId(), result.getContent().get(0).id());
  }

  @Test
  @DisplayName("Get All My Active Links - Success")
  void getAllMyActiveLinks_SUCCESS() {
    Link link1 = new Link();
    link1.setStatus(LinkStatus.ACTIVE);
    link1.setOwner(userRepository.findByUsername(testUsername).get());

    Page<Link> mockPage = new PageImpl<>(List.of(link1));

    when(linkRepository.findAllByOwnerIdAndStatus(eq(testUserId), eq(LinkStatus.ACTIVE), any(Pageable.class)))
        .thenReturn(mockPage);

    Page<LinkResponseDto> result = linkService.getAllMyActiveLinks(0, 10);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(LinkStatus.ACTIVE.toString(), result.getContent().get(0).status());
  }

  @Test
  @DisplayName("Delete Link - Success")
  void deleteLink_SUCCESS() {
    UUID linkId = UUID.randomUUID();
    Link link = new Link();
    link.setCode("abc123");
    link.setOwner(userRepository.findByUsername(testUsername).get());

    when(linkRepository.findById(linkId)).thenReturn(Optional.of(link));

    linkService.deleteLink(linkId);

    verify(linkRepository, times(1)).delete(link);
    verify(cacheEvictService, times(1)).evictLink("abc123");
  }



}

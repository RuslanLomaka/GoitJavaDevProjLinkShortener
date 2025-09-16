package org.decepticons.linkshortener.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.decepticons.linkshortener.api.dto.LinkResponseDto;
import org.decepticons.linkshortener.api.model.Link;
import org.decepticons.linkshortener.api.model.LinkStatus;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.LinkRepository;
import org.decepticons.linkshortener.api.service.impl.LinkServiceImpl;
import org.decepticons.linkshortener.api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;



/** * Unit tests for the LinkServiceImpl class.
 * These tests focus on the link management logic using mocks for dependencies.
 */
@ExtendWith(MockitoExtension.class)
public class LinkServiceImplMethodsTest {
  @Mock
  private LinkRepository linkRepository;

  @Mock
  private UserServiceImpl userServiceImpl;


  @InjectMocks
  private LinkServiceImpl linkService;

  private final UUID testUserId = UUID.randomUUID();
  private final String testUsername = "testuser";

  @BeforeEach
  void setUpSecurityContext() {

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(testUsername, null, List.of());
    SecurityContextHolder.getContext().setAuthentication(auth);


    User testUser = new User();
    testUser.setUsername(testUsername);
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Get All My Links - Success")
  void getAllMyLinksSuccess() {
    Link link1 = new Link();
    link1.setOwner(new User());

    Page<Link> mockPage = new PageImpl<>(List.of(link1));


    when(userServiceImpl.getCurrentUserId()).thenReturn(testUserId);
    when(linkRepository.findAllByOwnerId(eq(testUserId), any(Pageable.class)))
        .thenReturn(mockPage);

    Page<LinkResponseDto> result = linkService.getAllMyLinks(0, 10);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(link1.getId(), result.getContent().get(0).id());
  }

  @Test
  @DisplayName("Get All My Active Links - Success")
  void getAllMyActiveLinksSuccess() {
    Link link1 = new Link();
    link1.setStatus(LinkStatus.ACTIVE);
    link1.setOwner(new User());

    Page<Link> mockPage = new PageImpl<>(List.of(link1));

    when(userServiceImpl.getCurrentUserId()).thenReturn(testUserId);
    when(linkRepository.findAllByOwnerIdAndStatus(eq(testUserId),
        eq(LinkStatus.ACTIVE), any(Pageable.class)))
        .thenReturn(mockPage);

    Page<LinkResponseDto> result = linkService.getAllMyActiveLinks(0, 10);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(LinkStatus.ACTIVE.toString(), result.getContent().get(0).status());
  }

  @Test
  @DisplayName("Delete Link - Success")
  void deleteLinkSuccess() {
    User owner = new User();
    ReflectionTestUtils.setField(owner, "id", testUserId);
    owner.setUsername(testUsername);
    Link link = new Link();
    link.setCode("abc123");
    link.setOwner(owner);
    UUID linkId = UUID.randomUUID();

    when(userServiceImpl.getCurrentUserId()).thenReturn(testUserId);
    when(linkRepository.findById(linkId)).thenReturn(Optional.of(link));

    linkService.deleteLink(linkId);

    verify(linkRepository, times(1)).delete(link);
  }



}

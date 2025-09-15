package org.decepticons.linkshortener.api.v1.controller;

import org.decepticons.linkshortener.api.dto.LinkResponseDto;
import org.decepticons.linkshortener.api.service.impl.LinkServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LinkCrudControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LinkServiceImpl linkServiceImpl;

  @Test
  @WithMockUser(username = "testuser", roles = {"USER"})
  void testGetAllMyLinks() throws Exception {
    LinkResponseDto dto = new LinkResponseDto(
        UUID.randomUUID(),
        "abc123",
        "http://google.com",
        Instant.now(),
        Instant.now(),
        0L,
        "ACTIVE",
        UUID.randomUUID()
    );

    Page<LinkResponseDto> page = new PageImpl<>(
        Collections.singletonList(dto),
        PageRequest.of(0, 10),
        1
    );

    when(linkServiceImpl.getAllMyLinks(anyInt(), anyInt())).thenReturn(page);

    mockMvc.perform(get("/api/v1/links/my_all_links")
            .param("page", "0")
            .param("size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].originalUrl").value("http://google.com"))
        .andExpect(jsonPath("$.content[0].code").value("abc123"))
        .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
  }

  @Test
  @WithMockUser(username = "testuser", roles = {"USER"})
  void testGetAllMyActiveLinks() throws Exception {
    LinkResponseDto dto = new LinkResponseDto(
        UUID.randomUUID(),
        "xyz789",
        "http://google.com",
        Instant.now(),
        Instant.now(),
        5L,
        "ACTIVE",
        UUID.randomUUID()
    );

    Page<LinkResponseDto> page = new PageImpl<>(
        Collections.singletonList(dto),
        PageRequest.of(0, 10),
        1
    );

    when(linkServiceImpl.getAllMyActiveLinks(anyInt(), anyInt())).thenReturn(page);

    mockMvc.perform(get("/api/v1/links/my_all_active_links")
            .param("page", "0")
            .param("size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].originalUrl").value("http://google.com"))
        .andExpect(jsonPath("$.content[0].code").value("xyz789"))
        .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
  }

  @Test
  @WithMockUser(username = "testuser", roles = {"USER"})
  void testDeleteLinkSuccess() throws Exception {
    ResponseEntity<Void> responseEntity = ResponseEntity.noContent().build();
    UUID existingId = UUID.randomUUID();

    when(linkServiceImpl.deleteLink(existingId))
        .thenReturn(existingId.toString());

    mockMvc.perform(delete("/api/v1/links/delete/{id}", existingId.toString()))
        .andExpect(status().isNoContent());


    verify(linkServiceImpl, times(1)).deleteLink(existingId);
  }

}

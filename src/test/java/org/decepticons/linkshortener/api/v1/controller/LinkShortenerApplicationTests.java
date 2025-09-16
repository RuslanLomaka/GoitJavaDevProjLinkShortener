package org.decepticons.linkshortener.api.v1.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.decepticons.linkshortener.api.security.jwt.JwtAuthenticationFilter;
import org.decepticons.linkshortener.api.security.jwt.JwtTokenUtil;
import org.decepticons.linkshortener.api.v1.controller.unversioned.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest

@AutoConfigureMockMvc(addFilters = false)
class LinkShortenerApplicationTests {

  @Autowired ApplicationContext context;
  @MockitoBean
  JwtAuthenticationFilter jwtFilter;
  @MockitoBean
  JwtTokenUtil jwtTokenUtil;

  @Test
  void contextLoads() {
    assertThat(context).isNotNull();
  }

  @Test
void healthControllerIsLoaded() {
    assertThat(context.getBean(
            HealthController.class)).isNotNull();
  }
}

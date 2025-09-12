package org.decepticons.linkshortener.api.v1;

import org.decepticons.linkshortener.api.controller.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest

@AutoConfigureMockMvc(addFilters = false)
class LinkShortenerApplicationTests {

    @Autowired ApplicationContext context;

    @MockitoBean
    org.decepticons.linkshortener.api.security.jwt.JwtAuthenticationFilter jwtFilter;
    @MockitoBean  org.decepticons.linkshortener.api.security.jwt.JwtTokenUtil jwtTokenUtil;

    @Test
    void contextLoads() { assertThat(context).isNotNull(); }

    @Test
    void healthControllerIsLoaded() {
        assertThat(context.getBean(
                HealthController.class)).isNotNull();
    }
}

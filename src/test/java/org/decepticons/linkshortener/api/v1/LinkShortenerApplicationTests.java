package org.decepticons.linkshortener.api.v1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest

@AutoConfigureMockMvc(addFilters = false)
class LinkShortenerApplicationTests {

    @Autowired ApplicationContext context;

    @MockBean
    org.decepticons.linkshortener.api.security.jwt.JwtAuthenticationFilter jwtFilter;
    @MockBean org.decepticons.linkshortener.api.security.jwt.JwtTokenUtil jwtTokenUtil;

    @Test
    void contextLoads() { assertThat(context).isNotNull(); }

    @Test
    void healthControllerIsLoaded() {
        assertThat(context.getBean(
                org.decepticons.linkshortener.api.v1.controller.HealthController.class)).isNotNull();
    }
}

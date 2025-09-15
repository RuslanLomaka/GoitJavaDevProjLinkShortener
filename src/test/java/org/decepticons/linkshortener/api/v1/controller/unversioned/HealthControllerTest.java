package org.decepticons.linkshortener.api.v1.controller.unversioned;

import org.decepticons.linkshortener.api.security.jwt.JwtAuthenticationFilter;
import org.decepticons.linkshortener.api.security.jwt.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HealthController.class)
@AutoConfigureMockMvc(addFilters = false) // disable Spring Security filters
class HealthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean private JwtTokenUtil jwtTokenUtil;

    @Test
    void health_returnsUp() throws Exception {
        mvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value("UP"));
    }
}

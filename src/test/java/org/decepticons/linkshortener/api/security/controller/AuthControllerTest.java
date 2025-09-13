package org.decepticons.linkshortener.api.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.decepticons.linkshortener.api.dto.RegistrationRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the AuthController using Testcontainers and a live database.
 * These tests verify the full application stack's behavior for authentication
 * and registration, from the controller to the real database.
 */
@Testcontainers
@SpringBootTest
@DisplayName("Auth Controller Integration Tests with Testcontainers")
class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Use a PostgreSQL container for the tests
    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    // Dynamically set the data source properties using the running container's details
    @DynamicPropertySource
    static void setDatasourceProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration/postgresql");
    }

    @BeforeEach
    void setup() {
        // Build MockMvc from the full application context
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("given a new user, when registering, then the user is successfully created in the database")
    void givenNewUser_whenRegistering_thenUserIsCreated() throws Exception {
        // Given a new registration request
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        requestDto.setUsername("testuser_new");
        requestDto.setPassword("Password123!");

        // When the registration endpoint is called
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("testuser_new"));
    }

    @Test
    @DisplayName("given an existing user, when registering, then returns 409 Conflict")
    void givenExistingUser_whenRegistering_thenReturnsConflict() throws Exception {
        // First, register a user to ensure they exist in the database
        RegistrationRequestDto initialUser = new RegistrationRequestDto();
        initialUser.setUsername("existinguser");
        initialUser.setPassword("Password123!");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(initialUser)));

        // Then, attempt to register the same user again
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initialUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User with username 'existinguser' already exists"));
    }
}

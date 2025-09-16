package org.decepticons.linkshortener.api.security.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.decepticons.linkshortener.api.dto.AuthRequestDto;
import org.decepticons.linkshortener.api.dto.RegistrationRequestDto;
import org.decepticons.linkshortener.api.model.User;
import org.decepticons.linkshortener.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration tests for the AuthController using Testcontainers.
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

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private final ObjectMapper objectMapper = new ObjectMapper();

  // Use a PostgreSQL container for the tests
  @Container
 public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
      "postgres:16-alpine")
      .withDatabaseName("testdb")
      .withUsername("testuser")
      .withPassword("testpass");

  // Dynamically set the data source properties using the running container's details
  @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    registry.add("spring.flyway.enabled", () -> "true");
    registry.add("spring.flyway.locations", () -> "classpath:db/migration/postgresql");
  }

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .build();
    userRepository.deleteAll(); // Clean up before each test
  }

  @Test
  @DisplayName("given a new user, when registering, then the user "
      + "is successfully created and returns 200 OK")
  void givenNewUser_whenRegistering_thenUserIsCreated() throws Exception {
    // Given a new registration request
    RegistrationRequestDto requestDto = new RegistrationRequestDto();
    requestDto.setUsername("testuser_new");
    requestDto.setPassword("Password123!");

    // When the registration endpoint is called
    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value("testuser_new"));

    // Then verify the user exists in the database
    assertTrue(userRepository.findByUsername("testuser_new").isPresent());
  }

  @Test
  @DisplayName("given an existing user, when registering, then returns 409 Conflict")
  void givenExistingUser_whenRegistering_thenReturnsConflict() throws Exception {
    // Given an existing user in the database
    User existingUser = new User();
    existingUser.setUsername("existinguser");
    existingUser.setPasswordHash(passwordEncoder.encode("Password123!"));
    userRepository.save(existingUser);

    // When attempting to register the same user again
    RegistrationRequestDto requestDto = new RegistrationRequestDto();
    requestDto.setUsername("existinguser");
    requestDto.setPassword("Password123!");

    // Then an exception should be thrown
    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("User with username 'existinguser' already exists"));
  }

  @Test
  @DisplayName("given valid credentials, when logging in, then returns JWT tokens")
  void givenValidCredentials_whenLoggingIn_thenReturnsJwtTokens() throws Exception {
    // Given a registered user
    RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
    registrationRequestDto.setUsername("loginuser");
    registrationRequestDto.setPassword("Password123!");
    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registrationRequestDto)));

    // When logging in with valid credentials
    AuthRequestDto loginRequestDto = new AuthRequestDto();
    loginRequestDto.setUsername("loginuser");
    loginRequestDto.setPassword("Password123!");

    // Then the response should contain access and refresh tokens
    mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("loginuser"))
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists());
  }

  @Test
  @DisplayName("given invalid password, when logging in, then returns 401 Unauthorized")
  void givenInvalidPassword_whenLoggingIn_thenReturnsUnauthorized() throws Exception {
    // Given a registered user
    RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
    registrationRequestDto.setUsername("badpassuser");
    registrationRequestDto.setPassword("Password123!");
    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registrationRequestDto)));

    // When logging in with an invalid password
    AuthRequestDto loginRequestDto = new AuthRequestDto();
    loginRequestDto.setUsername("badpassuser");
    loginRequestDto.setPassword("WrongPassword");

    // Then the response should be 401 Unauthorized
    mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequestDto)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("given a valid refresh token, when refreshing, then returns new JWT tokens")
  void givenValidRefreshToken_whenRefreshing_thenReturnsNewTokens() throws Exception {
    // Given a registered user with a valid refresh token
    RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
    registrationRequestDto.setUsername("refreshuser");
    registrationRequestDto.setPassword("Password123!");
    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registrationRequestDto)));

    AuthRequestDto loginRequestDto = new AuthRequestDto();
    loginRequestDto.setUsername("refreshuser");
    loginRequestDto.setPassword("Password123!");
    MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequestDto)))
        .andReturn();

    String response = result.getResponse().getContentAsString();
    String refreshToken = objectMapper.readTree(response).get("refreshToken").asText();

    // When refreshing the token
    mockMvc.perform(post("/api/v1/auth/refresh")
            .header("Authorization", "Bearer " + refreshToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists());
  }

  @Test
  @DisplayName("given an invalid refresh token, when refreshing, then returns 401 Unauthorized")
  void givenInvalidRefreshToken_whenRefreshing_thenReturnsUnauthorized() throws Exception {
    // Given an invalid token
    String invalidToken = "invalid.token.123";

    // When refreshing with the invalid token
    mockMvc.perform(post("/api/v1/auth/refresh")
            .header("Authorization", "Bearer " + invalidToken))
        .andExpect(status().isUnauthorized());
  }
}
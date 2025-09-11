package org.decepticons.linkshortener.api.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.decepticons.linkshortener.api.dto.AuthRequestDto;
import org.decepticons.linkshortener.api.dto.AuthResponseDto;
import org.decepticons.linkshortener.api.dto.RegistrationRequestDto;
import org.decepticons.linkshortener.api.exceptions.InvalidPasswordException;
import org.decepticons.linkshortener.api.exceptions.InvalidTokenException;
import org.decepticons.linkshortener.api.exceptions.UserAlreadyExistsException;
import org.decepticons.linkshortener.api.exceptions.UserNotFoundException;
import org.decepticons.linkshortener.api.security.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the AuthController.
 * These tests verify the REST API endpoints' behavior for authentication and registration,
 * with the service layer mocked.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Controller Integration Tests")
class AuthControllerTest {

  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Mock
  private AuthService authService;

  @InjectMocks
  private AuthController authController;

  @BeforeEach
  void setup() {
    // Given
    mockMvc = MockMvcBuilders.standaloneSetup(authController)
        .setControllerAdvice(new org.decepticons.linkshortener.api.exceptions.GlobalExceptionHandler())
        .build();
  }

  @Test
  @DisplayName("given a registration request, when registering, then returns 200 OK")
  void givenRegistrationRequest_whenRegistering_thenReturnsOk() throws Exception {
    // Given
    RegistrationRequestDto requestDto = new RegistrationRequestDto();
    requestDto.setUsername("testuser");
    requestDto.setPassword("Password123!");
    String expectedUsername = "testuser";

    when(authService.registerUser(any(RegistrationRequestDto.class))).thenReturn(expectedUsername);

    // When & Then
    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(expectedUsername));
  }

  @Test
  @DisplayName("given an existing username, when registering, then returns 409 Conflict")
  void givenExistingUsername_whenRegistering_thenReturnsConflict() throws Exception {
    // Given
    RegistrationRequestDto requestDto = new RegistrationRequestDto();
    requestDto.setUsername("existinguser");
    requestDto.setPassword("Password123!");
    String expectedErrorMessage = "User with username 'existinguser' already exists";

    doThrow(new UserAlreadyExistsException("existinguser")).when(authService).registerUser(any(RegistrationRequestDto.class));

    // When & Then
    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

  @Test
  @DisplayName("given an invalid password, when registering, then returns 400 Bad Request")
  void givenInvalidPassword_whenRegistering_thenReturnsBadRequest() throws Exception {
    // Given
    RegistrationRequestDto requestDto = new RegistrationRequestDto();
    requestDto.setUsername("newuser");
    requestDto.setPassword("short");
    String expectedErrorMessage = "Password does not meet complexity requirements";

    doThrow(new InvalidPasswordException(expectedErrorMessage)).when(authService).registerUser(any(RegistrationRequestDto.class));

    // When & Then
    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

  @Test
  @DisplayName("given a valid login request, when logging in, then returns 200 OK with tokens")
  void givenValidLoginRequest_whenLoggingIn_thenReturnsTokens() throws Exception {
    // Given
    AuthRequestDto requestDto = new AuthRequestDto();
    requestDto.setUsername("testuser");
    requestDto.setPassword("Password123!");
    AuthResponseDto expectedResponseDto = new AuthResponseDto("testuser", Collections.emptyList(), "access.token", "refresh.token");

    when(authService.login(any(AuthRequestDto.class))).thenReturn(expectedResponseDto);

    // When & Then
    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value(expectedResponseDto.getAccessToken()))
        .andExpect(jsonPath("$.refreshToken").value(expectedResponseDto.getRefreshToken()));
  }

  @Test
  @DisplayName("given an invalid login request, when logging in, then returns 404 Not Found")
  void givenInvalidLoginRequest_whenLoggingIn_thenReturnsNotFound() throws Exception {
    // Given
    AuthRequestDto requestDto = new AuthRequestDto();
    requestDto.setUsername("invaliduser");
    requestDto.setPassword("wrongpassword");
    String expectedErrorMessage = "User with username 'invaliduser' not found";

    doThrow(new UserNotFoundException("invaliduser")).when(authService).login(any(AuthRequestDto.class));

    // When & Then
    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

  @Test
  @DisplayName("given a valid refresh token, when refreshing, then returns 200 OK with new tokens")
  void givenValidRefreshToken_whenRefreshing_thenReturnsNewTokens() throws Exception {
    // Given
    AuthResponseDto expectedResponseDto = new AuthResponseDto();
    when(authService.refreshToken(any(String.class))).thenReturn(expectedResponseDto);

    // When & Then
    mockMvc.perform(post("/auth/refresh")
            .header("Authorization", "Bearer valid.refresh.token")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("given an invalid refresh token, when refreshing, then returns 401 Unauthorized")
  void givenInvalidRefreshToken_whenRefreshing_thenReturnsUnauthorized() throws Exception {
    // Given
    String expectedErrorMessage = "invalid token";
    doThrow(new InvalidTokenException(expectedErrorMessage)).when(authService).refreshToken(any(String.class));

    // When & Then
    mockMvc.perform(post("/auth/refresh")
            .header("Authorization", "Bearer invalid.refresh.token")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }
}
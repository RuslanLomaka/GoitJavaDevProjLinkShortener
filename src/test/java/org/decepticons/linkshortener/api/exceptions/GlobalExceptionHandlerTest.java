package org.decepticons.linkshortener.api.exceptions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.jsonwebtoken.JwtException;
import org.decepticons.linkshortener.api.exception.ExpiredTokenException;
import org.decepticons.linkshortener.api.exception.InvalidPasswordException;
import org.decepticons.linkshortener.api.exception.InvalidTokenException;
import org.decepticons.linkshortener.api.exception.UserAlreadyExistsException;
import org.decepticons.linkshortener.api.exception.UserNotFoundException;
import org.decepticons.linkshortener.api.v1.controller.unversioned.GlobalExceptionHandlerController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Unit tests for the GlobalExceptionHandler.
 * These tests ensure that all custom exceptions are handled correctly
 * and return the expected HTTP status codes and error bodies.
 */
@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    // Given
    mockMvc = MockMvcBuilders
        .standaloneSetup(new TestController())
        .setControllerAdvice(new GlobalExceptionHandlerController())
        .build();
  }

  @Test
  @DisplayName("given UserAlreadyExistsException, when thrown, then returns 409 CONFLICT")
  void givenUserAlreadyExistsException_whenThrown_thenReturnConflict() throws Exception {
    // Given
    String expectedMessage = "User with username 'testuser' already exists";
    String expectedError = "User Already Exists";
    int expectedStatus = HttpStatus.CONFLICT.value();

    // When & Then
    mockMvc.perform(get("/test-user-exists"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(expectedStatus))
        .andExpect(jsonPath("$.error").value(expectedError))
        .andExpect(jsonPath("$.message").value(expectedMessage));
  }

  @Test
  @DisplayName("given UserNotFoundException, when thrown, then returns 404 NOT FOUND")
  void givenUserNotFoundException_whenThrown_thenReturnNotFound() throws Exception {
    // Given
    String expectedMessage = "User with username 'nonexistent' not found";
    String expectedError = "User Not Found";
    int expectedStatus = HttpStatus.NOT_FOUND.value();

    // When & Then
    mockMvc.perform(get("/test-user-not-found"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(expectedStatus))
        .andExpect(jsonPath("$.error").value(expectedError))
        .andExpect(jsonPath("$.message").value(expectedMessage));
  }

  @Test
  @DisplayName("given InvalidPasswordException, when thrown, then returns 400 BAD REQUEST")
  void givenInvalidPasswordException_whenThrown_thenReturnBadRequest() throws Exception {
    // Given
    String expectedMessage = "Password does not meet complexity requirements";
    String expectedError = "Invalid Password";
    int expectedStatus = HttpStatus.BAD_REQUEST.value();

    // When & Then
    mockMvc.perform(get("/test-invalid-password"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(expectedStatus))
        .andExpect(jsonPath("$.error").value(expectedError))
        .andExpect(jsonPath("$.message").value(expectedMessage));
  }

  @Test
  @DisplayName("given InvalidTokenException, when thrown, then returns 401 UNAUTHORIZED")
  void givenInvalidTokenException_whenThrown_thenReturnUnauthorized() throws Exception {
    // Given
    String expectedMessage = "Invalid Token";
    String expectedError = "Invalid Token";
    int expectedStatus = HttpStatus.UNAUTHORIZED.value();

    // When & Then
    mockMvc.perform(get("/test-invalid-token"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(expectedStatus))
        .andExpect(jsonPath("$.error").value(expectedError))
        .andExpect(jsonPath("$.message").value(expectedMessage));
  }

  @Test
  @DisplayName("given ExpiredTokenException, when thrown, then returns 401 UNAUTHORIZED")
  void givenExpiredTokenException_whenThrown_thenReturnUnauthorized() throws Exception {
    // Given
    String expectedMessage = "Expired token";
    String expectedError = "Expired Token";
    int expectedStatus = HttpStatus.UNAUTHORIZED.value();

    // When & Then
    mockMvc.perform(get("/test-expired-token"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(expectedStatus))
        .andExpect(jsonPath("$.error").value(expectedError))
        .andExpect(jsonPath("$.message").value(expectedMessage));
  }

  @Test
  @DisplayName("given a generic Exception, when thrown, then returns 500 INTERNAL SERVER ERROR")
  void givenGenericException_whenThrown_thenReturnInternalServerError() throws Exception {
    // Given
    String expectedMessage = "Something went wrong";
    String expectedError = "Server Error";
    int expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();

    // When & Then
    mockMvc.perform(get("/test-generic-exception"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value(expectedStatus))
        .andExpect(jsonPath("$.error").value(expectedError))
        .andExpect(jsonPath("$.message").value(expectedMessage));
  }

  /**
   * A simple dummy controller to trigger the exceptions for testing.
   */
  @RestController
  private static class TestController {

    @GetMapping("/test-authentication-exception")
    public void testAuthenticationException() {
      throw new BadCredentialsException("Invalid username or password");
    }

    @GetMapping("/test-jwt-exception")
    public void testJwtException() {
      throw new JwtException("JWT expired");
    }

    @GetMapping("/test-user-exists")
    public void testUserExists() {
      throw new UserAlreadyExistsException("testuser");
    }

    @GetMapping("/test-user-not-found")
    public void testUserNotFound() {
      throw new UserNotFoundException("nonexistent");
    }

    @GetMapping("/test-invalid-password")
    public void testInvalidPassword() {
      throw new InvalidPasswordException("Password does not meet complexity requirements");
    }

    @GetMapping("/test-invalid-token")
    public void testInvalidToken() {
      throw new InvalidTokenException("Invalid Token");
    }

    @GetMapping("/test-expired-token")
    public void testExpiredToken() {
      throw new ExpiredTokenException("Expired token");
    }

    @GetMapping("/test-generic-exception")
    public void testGenericException() throws Exception {
      throw new Exception("Something went wrong");
    }
  }
}
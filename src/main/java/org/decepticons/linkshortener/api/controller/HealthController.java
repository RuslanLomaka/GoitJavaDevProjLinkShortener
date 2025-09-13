package org.decepticons.linkshortener.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for health check endpoint.

 * Provides a simple `/health` endpoint to check if the service is running.

 */
@RestController
public class HealthController {

  /**
   * Returns the current status of the service.
   *
   * @return ResponseEntity containing a Status object with value "UP".
   */
  @GetMapping("/health")
  @Operation(
      summary = "Health check",
      description = "Simple endpoint to check if the service is alive and responding.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Service is healthy",
              content = @Content(
                  schema = @Schema(implementation = Status.class)
              )
          )
      }
  )
  public ResponseEntity<Status> health() {
    return ResponseEntity.ok().body(new Status("UP"));
  }

  /**
   * Simple DTO used to represent the health status.
   *
   * @param status textual status value, e.g. "UP".
   */
  private record Status(String status) {
  }
}

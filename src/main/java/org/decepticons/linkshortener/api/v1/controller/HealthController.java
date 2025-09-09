package org.decepticons.linkshortener.api.v1.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for health check endpoint.
 * Provides a simple /health endpoint to check if the service is running.
 */
@RestController
public class HealthController {


  /**
   * Returns the current status of the service.
   *
   * @return ResponseEntity containing a Status object with value "UP".
   */
  @GetMapping("/api/v1/health")
  public ResponseEntity<?> health() {
    return ResponseEntity.ok().body(new Status("UP"));
  }


  /**
   * Status record used to represent the health status.
   */
  private record Status(String status) {
  }
}

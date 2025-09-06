package org.decepticons.linkshortener.api.v1.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// If you use springdoc-openapi, you can add @Operation(summary="Health check")
@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok().body(new Status("UP"));
    }

    private record Status(String status) {}
}

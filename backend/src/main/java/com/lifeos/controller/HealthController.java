package com.lifeos.controller;

import com.lifeos.dto.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @Value("${app.version:1.0.0}")
    private String version;

    @GetMapping
    public ResponseEntity<HealthResponse> checkHealth() {
        HealthResponse response = HealthResponse.builder()
                .status("UP")
                .timestamp(Instant.now())
                .version(version)
                .build();
        return ResponseEntity.ok(response);
    }
}

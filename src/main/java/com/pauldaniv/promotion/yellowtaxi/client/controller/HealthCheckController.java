package com.pauldaniv.promotion.yellowtaxi.client.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health_check")
public class HealthCheckController {
    @GetMapping
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok("ok\n");
    }
}

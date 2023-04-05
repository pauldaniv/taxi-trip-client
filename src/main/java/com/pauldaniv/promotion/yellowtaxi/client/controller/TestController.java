package com.pauldaniv.promotion.yellowtaxi.client.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ping")
public class TestController {
    @PostMapping
    public ResponseEntity<?> ping(String body) {
        return ResponseEntity.ok(body);
    }
}

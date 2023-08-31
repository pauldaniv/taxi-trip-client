package com.pauldaniv.promotion.yellowtaxi.client.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionCheckService {
    private final FacadeService facadeService;

    public void isSessionActive() {
        if (!facadeService.isSessionActive()) {
            throw new RuntimeException("No active session detected");
        }
    }
}

package com.pauldaniv.promotion.yellowtaxi.client;

import com.pauldaniv.promotion.yellowtaxi.client.service.cmd.CmdResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class TaxiTripClientApplication implements CommandLineRunner {

    private final CmdResolver cmdService;

    public static void main(String[] args) {
        SpringApplication.run(TaxiTripClientApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            cmdService.handle(args);
        } catch (Exception e) {
            log.error("Failed to execute command. Reason: {}", e.getMessage());
        }
    }
}

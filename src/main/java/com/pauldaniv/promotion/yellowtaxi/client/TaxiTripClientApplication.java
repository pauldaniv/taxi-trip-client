package com.pauldaniv.promotion.yellowtaxi.client;

import com.pauldaniv.promotion.yellowtaxi.client.service.CmdResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class TaxiTripClientApplication implements CommandLineRunner {

    private final CmdResolver cmdService;

    public static void main(String[] args) {
        SpringApplication.run(TaxiTripClientApplication.class, args);
    }

    @Override
    public void run(String... args) {
        cmdService.handle(args);
    }
}

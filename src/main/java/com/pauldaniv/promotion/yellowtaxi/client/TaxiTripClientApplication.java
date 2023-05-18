package com.pauldaniv.promotion.yellowtaxi.client;

import com.pauldaniv.promotion.yellowtaxi.client.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class TaxiTripClientApplication implements CommandLineRunner {
	private final TestService testService;
	public static void main(String[] args) {
		SpringApplication.run(TaxiTripClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		testService.doSomething();
	}
}

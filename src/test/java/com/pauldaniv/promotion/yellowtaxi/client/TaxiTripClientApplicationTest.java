package com.pauldaniv.promotion.yellowtaxi.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@Slf4j
public class TaxiTripClientApplicationTest extends AbstractTestNGSpringContextTests {
    @Test
    public void loadsContext() {
        log.info("msg=context_loaded");
    }
}

package com.pauldaniv.promotion.yellowtaxi.client.service.cmd;

import com.pauldaniv.promotion.yellowtaxi.client.model.PerformanceStats;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PerformanceServiceTest extends AbstractTestNGSpringContextTests {

    @MockBean
    private EventSenderService eventSenderService;

    @BeforeTest
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Autowired
    private PerformanceService performanceService;

    @Test
    public void runsPerformanceCheckSuccessfully() {
        when(eventSenderService.sendEvents(29L, 10L))
                .thenReturn(PerformanceStats.builder()
                        .avgRequestsPerSecond(new BigDecimal("100"))
                        .build());
        performanceService.runCommand(List.of("--count", "29", "--concurrency", "10"));
    }

    @Test
    public void failsPerformanceCheck() {
        when(eventSenderService.sendEvents(29L, 10L))
                .thenReturn(PerformanceStats.builder()
                        .avgRequestsPerSecond(new BigDecimal("2"))
                        .build());

        assertThatThrownBy(() -> performanceService
                .runCommand(List.of("--count", "29", "--concurrency", "10")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Performance requirement did not meet");
    }

    @AfterTest
    public void teardown() {
        reset(eventSenderService);
    }
}

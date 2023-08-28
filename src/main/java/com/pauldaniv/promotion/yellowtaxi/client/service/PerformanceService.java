package com.pauldaniv.promotion.yellowtaxi.client.service;

import com.pauldaniv.promotion.yellowtaxi.client.model.PerformanceStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service("performance")
@RequiredArgsConstructor
public class PerformanceService implements CmdService {

    private final EventSenderService eventSenderService;

    @Override
    public void runCommand(List<String> params) {
        log.info("Collecting performance data...");
        final PerformanceStats performanceStats = eventSenderService.sendEvents(5000L, 25L);
        log.info("Done collecting performance data!");
        final BigDecimal avgRequestsPerSecond = performanceStats.getAvgRequestsPerSecond();
        final BigDecimal threshold = new BigDecimal("70");
        log.info("Verifying that average requests per second is greater than: {}", threshold);
        assert avgRequestsPerSecond.compareTo(threshold) > 0;
        log.info("Success!");
    }
}

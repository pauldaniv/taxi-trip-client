package com.pauldaniv.promotion.yellowtaxi.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceStats {
    private Long successfulRequests;
    private Long failedRequests;
    private BigDecimal secondsTook;
    private BigDecimal avgRequestsPerSecond;
}

package com.pauldaniv.promotion.yellowtaxi.client.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RunEventParams implements RunEvent{
    private Integer count;
}

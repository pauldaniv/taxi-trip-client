package com.pauldaniv.promotion.yellowtaxi.client.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommandSpec {
    private String typeSpec;
    private String description;
    private String defaultValue;
}

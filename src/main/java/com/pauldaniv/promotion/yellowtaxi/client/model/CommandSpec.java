package com.pauldaniv.promotion.yellowtaxi.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandSpec {
    private String typeSpec;
    private String description;
    private String defaultValue;
}

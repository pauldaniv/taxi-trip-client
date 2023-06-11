package com.pauldaniv.promotion.yellowtaxi.client.config;

import com.pauldaniv.promotion.yellowtaxi.client.service.CmdService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class TotalsConfig {
    @Bean
    public Map<String, CmdService> processorMap(ApplicationContext context) {
        return context.getBeansOfType(CmdService.class);
    }
}

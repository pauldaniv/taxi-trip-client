package com.pauldaniv.promotion.yellowtaxi.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "event.file")
public class AppConfig {
    private String fileKey;
    private String localBasePath;
    private String s3BasePath;
    private Boolean eventFileLocal;
}

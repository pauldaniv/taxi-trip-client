package com.pauldaniv.promotion.yellowtaxi.client.config;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsClientConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public AmazonS3 amazonSyncHttpClient() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(awsRegion)
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .build();
    }
}

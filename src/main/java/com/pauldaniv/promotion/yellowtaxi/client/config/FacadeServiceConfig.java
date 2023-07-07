package com.pauldaniv.promotion.yellowtaxi.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pauldaniv.promotion.yellowtaxi.facade.api.TaxiTripFacadeAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class FacadeServiceConfig {

    @Value("${services.facade-api.url}")
    private String facadeApiUrl;

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public TaxiTripFacadeAPI createFacadeAPI(ObjectMapper objectMapper) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(facadeApiUrl)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
        return retrofit.create(TaxiTripFacadeAPI.class);
    }
}

package com.pauldaniv.promotion.yellowtaxi.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.pauldaniv.promotion.yellowtaxi.facade.TaxiTripFacadeAPI;
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
        return new ObjectMapper();
    }

    @Bean
    public TaxiTripFacadeAPI createFacadeAPI(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JSR310Module());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(facadeApiUrl)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
        return retrofit.create(TaxiTripFacadeAPI.class);
    }
}

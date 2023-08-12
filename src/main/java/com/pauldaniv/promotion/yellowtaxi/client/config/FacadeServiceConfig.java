package com.pauldaniv.promotion.yellowtaxi.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pauldaniv.promotion.yellowtaxi.facade.api.TaxiTripFacadeAPI;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public TaxiTripFacadeAPI createFacadeAPI(final ObjectMapper objectMapper,
                                             final OkHttpClient okHttpClient) {
        final Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(facadeApiUrl)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
        return retrofit.create(TaxiTripFacadeAPI.class);
    }

    @Bean
    public OkHttpClient okHttpClient() {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            final Path path = Paths.get("token.txt");
            final String token;
            if (Files.exists(path)) {
                token = Files.readString(path);
            } else {
                return builder.build();
            }
            if (Strings.isNotBlank(token)) {
                builder.addInterceptor(chain -> {
                    final Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(newRequest);
                });
            }
            return builder.build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

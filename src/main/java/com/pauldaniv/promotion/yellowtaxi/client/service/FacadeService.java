package com.pauldaniv.promotion.yellowtaxi.client.service;

import com.pauldaniv.promotion.yellowtaxi.facade.api.TaxiTripFacadeAPI;
import com.pauldaniv.promotion.yellowtaxi.facade.model.AuthRequest;
import com.pauldaniv.promotion.yellowtaxi.facade.model.AuthResponse;
import com.pauldaniv.promotion.yellowtaxi.facade.model.ResponseData;
import com.pauldaniv.promotion.yellowtaxi.facade.model.TotalsResponse;
import com.pauldaniv.promotion.yellowtaxi.facade.model.TripRequest;
import com.pauldaniv.promotion.yellowtaxi.facade.model.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacadeService {

    private final TaxiTripFacadeAPI taxiTripFacadeAPI;

    public ResponseData sendEvent(TripRequest event) {
        try {
            final Response<ResponseData> response = taxiTripFacadeAPI.pushTaxiTrip(event).execute();
            if (response.code() == 401) {
                throw new UnauthorizedException();
            }
            return response.body();
        } catch (IOException e) {
            log.error("Failed to execute request. Error: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public TotalsResponse getTotals(final Integer year, final Integer month, final Integer day) {
        try {
            return taxiTripFacadeAPI.getTotals(year, month, day).execute().body();
        } catch (IOException e) {
            log.error("Failed to execute request. Error: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public AuthResponse login(AuthRequest authRequest) {
        try {
            return taxiTripFacadeAPI.login(authRequest).execute().body();
        } catch (IOException e) {
            log.error("Failed to execute request. Error: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Boolean isSessionActive() {
        try {
            return Optional.ofNullable(taxiTripFacadeAPI.identity().execute().body())
                    .map(AuthResponse::getEmail)
                    .isPresent();
        } catch (Exception e) {
            log.error("Failed to execute request. Error: {}", e.getMessage(), e);
            return false;
        }
    }
}

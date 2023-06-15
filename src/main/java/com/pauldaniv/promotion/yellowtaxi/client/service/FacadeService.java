package com.pauldaniv.promotion.yellowtaxi.client.service;

import com.pauldaniv.promotion.yellowtaxi.facade.TaxiTripFacadeAPI;
import com.pauldaniv.promotion.yellowtaxi.model.ResponseData;
import com.pauldaniv.promotion.yellowtaxi.model.TotalsResponse;
import com.pauldaniv.promotion.yellowtaxi.model.TripRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacadeService {

    private final TaxiTripFacadeAPI taxiTripFacadeAPI;

    public ResponseData sendEvent(TripRequest event) {
        try {
            return taxiTripFacadeAPI.pushTaxiTrip(event).execute().body();
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
}

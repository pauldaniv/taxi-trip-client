package com.pauldaniv.promotion.yellowtaxi.client.service;

import com.pauldaniv.promotion.yellowtaxi.facade.api.TaxiTripFacadeAPI;
import com.pauldaniv.promotion.yellowtaxi.facade.model.AuthResponse;
import com.pauldaniv.promotion.yellowtaxi.facade.model.ResponseData;
import com.pauldaniv.promotion.yellowtaxi.facade.model.TotalsResponse;
import com.pauldaniv.promotion.yellowtaxi.facade.model.UnauthorizedException;
import com.pauldaniv.promotion.yellowtaxi.model.TaxiTrip;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FacadeServiceTest extends AbstractTestNGSpringContextTests {

    @MockBean
    private TaxiTripFacadeAPI taxiTripFacadeAPI;

    @Mock
    private Call callMock;

    @Mock
    private Response responseMock;

    @Autowired
    private FacadeService facadeService;


    @BeforeTest
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void sendsEventSuccessfully() throws IOException {
        when(taxiTripFacadeAPI.pushTaxiTrip(any())).thenReturn(callMock);
        when(callMock.execute()).thenReturn(responseMock);
        when(responseMock.code()).thenReturn(200);
        when(responseMock.body()).thenReturn(ResponseData.builder()
                .message("test")
                .build());

        assertThat(facadeService.sendEvent(new TaxiTrip()).getMessage()).isEqualTo("test");
    }

    @Test
    public void failsToSendEventWithUnauthorized() throws IOException {
        when(taxiTripFacadeAPI.pushTaxiTrip(any())).thenReturn(callMock);
        when(callMock.execute()).thenReturn(responseMock);
        when(responseMock.code()).thenReturn(401);

        assertThatThrownBy(() -> facadeService
                .sendEvent(new TaxiTrip()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("unauthorized");
    }

    @Test
    public void failsToSendEventWithIoError() throws IOException {
        when(taxiTripFacadeAPI.pushTaxiTrip(any())).thenReturn(callMock);
        when(callMock.execute()).thenThrow(new IOException("test"));

        assertThatThrownBy(() -> facadeService
                .sendEvent(new TaxiTrip()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to execute request");
    }

    @Test
    public void getsTotalsSuccessfully() throws IOException {
        when(taxiTripFacadeAPI.getTotals(2018, 10, 2))
                .thenReturn(callMock);
        when(callMock.execute()).thenReturn(responseMock);
        when(responseMock.code()).thenReturn(200);
        when(responseMock.body()).thenReturn(TotalsResponse.builder()
                .total(new BigDecimal("120"))
                .build());

        assertThat(facadeService.getTotals(2018, 10, 2).getTotal())
                .isEqualTo("120");
    }

    @Test
    public void failsToGetTotalsIoError() throws IOException {
        when(taxiTripFacadeAPI.getTotals(2018, 10, 2)).thenReturn(callMock);
        when(callMock.execute()).thenThrow(new IOException("test"));

        assertThatThrownBy(() -> facadeService
                .getTotals(2018, 10, 2))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to execute request");
    }

    @Test
    public void logsInSuccessfully() throws IOException {
        when(taxiTripFacadeAPI.login(any()))
                .thenReturn(callMock);
        when(callMock.execute()).thenReturn(responseMock);
        when(responseMock.body()).thenReturn(AuthResponse.builder()
                .accessToken("test.token")
                .build());

        assertThat(facadeService.login(any()).getAccessToken())
                .isEqualTo("test.token");
    }

    @Test
    public void failsToLoginIoError() throws IOException {
        when(taxiTripFacadeAPI.login(any())).thenReturn(callMock);
        when(callMock.execute()).thenThrow(new IOException("test"));

        assertThatThrownBy(() -> facadeService
                .login(any()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to execute request");
    }

    @Test
    public void checksIsSessionActiveSuccessfully() throws IOException {
        when(taxiTripFacadeAPI.identity())
                .thenReturn(callMock);
        when(callMock.execute()).thenReturn(responseMock);
        when(responseMock.body()).thenReturn(AuthResponse.builder()
                .email("test.email@yellow-taxi.com")
                .build());

        assertThat(facadeService.isSessionActive()).isTrue();
    }

    @Test
    public void failsToCheckSessionError() throws IOException {
        when(taxiTripFacadeAPI.identity()).thenReturn(callMock);
        when(callMock.execute()).thenThrow(new IOException("test"));

        assertThat(facadeService.isSessionActive()).isFalse();
    }

    @AfterMethod
    public void teardown() {
        reset(taxiTripFacadeAPI, callMock, responseMock);
    }
}

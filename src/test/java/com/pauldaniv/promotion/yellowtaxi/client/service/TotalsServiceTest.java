package com.pauldaniv.promotion.yellowtaxi.client.service;

import com.pauldaniv.promotion.yellowtaxi.facade.model.TotalsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TotalsServiceTest extends AbstractTestNGSpringContextTests {

    @MockBean
    private FacadeService facadeService;
    @MockBean
    private SessionCheckService sessionCheckService;


    @Autowired
    private TotalsService totalsService;


    @Test
    public void failsWithMissingSession() {
        doThrow(new RuntimeException("test"))
                .when(sessionCheckService).isSessionActive();

        assertThatThrownBy(() -> totalsService
                .runCommand(List.of("--day", "29", "--month", "10", "--year", "2018")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("test");
    }


    @Test
    public void sendsEventsSuccessfully() {
        when(facadeService.getTotals(any(), any(), any()))
                .thenReturn(TotalsResponse.builder().build());

        totalsService
                .runCommand(List.of("--day", "29", "--month", "10", "--year", "2018"));
    }

    @AfterTest
    public void teardown() {
        reset(facadeService, sessionCheckService);
    }
}

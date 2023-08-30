package com.pauldaniv.promotion.yellowtaxi.client.service.cmd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.pauldaniv.promotion.yellowtaxi.client.service.FacadeService;
import com.pauldaniv.promotion.yellowtaxi.client.service.SessionCheckService;
import com.pauldaniv.promotion.yellowtaxi.facade.model.TotalsResponse;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;
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
    @MockBean
    private ObjectMapper objectMapper;

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
    public void failsWithInvalidJson() throws JsonProcessingException {
        when(facadeService.getTotals(2018, 10, 2018))
                .thenReturn(TotalsResponse.builder().build());
        final ObjectWriter mockWriter = Mockito.mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter())
                .thenReturn(mockWriter);
        when(mockWriter.writeValueAsString(any()))
                .thenThrow(new RuntimeException("test"));
        totalsService
                .runCommand(List.of("--day", "29", "--month", "10", "--year", "2018"));
    }


    @Test
    public void sendsEventsSuccessfully() throws JsonProcessingException {
        when(facadeService.getTotals(2018, 10, 2018))
                .thenReturn(TotalsResponse.builder().build());
        final ObjectWriter mockWriter = Mockito.mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter())
                .thenReturn(mockWriter);
        when(mockWriter.writeValueAsString(any()))
                .thenReturn("{\n\"total\":\"123\"\n}");
        totalsService
                .runCommand(List.of("--day", "30", "--month", "10", "--year", "2018"));
    }

    @AfterMethod
    public void teardown() {
        reset(facadeService, sessionCheckService, objectMapper);
    }
}

package com.pauldaniv.promotion.yellowtaxi.client.service;

import com.amazonaws.services.s3.AmazonS3;
import com.pauldaniv.promotion.yellowtaxi.client.model.PerformanceStats;
import com.pauldaniv.promotion.yellowtaxi.facade.model.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EventSenderServiceTest extends AbstractTestNGSpringContextTests {

    @MockBean
    private FacadeService facadeService;
    @MockBean
    private SessionCheckService sessionCheckService;
    @MockBean
    private AmazonS3 amazonS3;

    @Autowired
    private EventSenderService eventSenderService;


    @Test
    public void failsWithMissingSession() {
        doThrow(new RuntimeException("test"))
                .when(sessionCheckService).isSessionActive();

        assertThatThrownBy(() -> eventSenderService
                .runCommand(List.of("--count", "29", "--concurrency", "10")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("test");
    }

    @Test
    public void sendsEventsSuccessfully() {
        when(facadeService.sendEvent(any()))
                .thenReturn(ResponseData.builder().build());

        eventSenderService.runCommand(List.of("--count", "360", "--concurrency", "1"));

        verify(facadeService, times(360)).sendEvent(any());
    }

    @Test
    public void sendsEventsExceptionally() {
        when(facadeService.sendEvent(any()))
                .thenThrow(new RuntimeException("test"));
        //todo: fix flaky tests
        final PerformanceStats performanceStats = eventSenderService
                .sendEvents(8L, 10L);
        assertThat(performanceStats).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(PerformanceStats.builder()
                        .failedRequests(8L)
                        .successfulRequests(0L)
                        .build());

        verify(facadeService, times(8)).sendEvent(any());
    }

    @AfterTest
    public void teardown() {
        reset(facadeService, sessionCheckService, amazonS3);
    }
}

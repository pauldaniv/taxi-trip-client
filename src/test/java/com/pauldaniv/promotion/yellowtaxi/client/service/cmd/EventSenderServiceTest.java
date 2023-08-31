package com.pauldaniv.promotion.yellowtaxi.client.service.cmd;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.pauldaniv.promotion.yellowtaxi.client.config.AppConfig;
import com.pauldaniv.promotion.yellowtaxi.client.model.PerformanceStats;
import com.pauldaniv.promotion.yellowtaxi.client.service.FacadeService;
import com.pauldaniv.promotion.yellowtaxi.client.service.SessionCheckService;
import com.pauldaniv.promotion.yellowtaxi.client.service.cmd.EventSenderService;
import com.pauldaniv.promotion.yellowtaxi.facade.model.ResponseData;
import lombok.SneakyThrows;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    @MockBean
    private AppConfig appConfig;

    @Autowired
    private EventSenderService eventSenderService;


    @Test
    public void sendsEventsSuccessfullyWithConcurrencyGreaterThanEventCount() {
        when(facadeService.sendEvent(any()))
                .thenReturn(ResponseData.builder().build());
        when(appConfig.isEventFileLocal()).thenReturn(true);
        when(appConfig.getLocalBasePath()).thenReturn("src/test/resources");
        when(appConfig.getFileKey()).thenReturn("test_events.csv");
        eventSenderService.runCommand(List.of("--count", "200", "--concurrency", "202"));

        verify(facadeService, times(200)).sendEvent(any());
    }

    @Test
    public void sendsEventsSuccessfully() {
        when(facadeService.sendEvent(any()))
                .thenReturn(ResponseData.builder().build());
        when(appConfig.isEventFileLocal()).thenReturn(true);
        when(appConfig.getLocalBasePath()).thenReturn("src/test/resources");
        when(appConfig.getFileKey()).thenReturn("test_events.csv");
        eventSenderService.runCommand(List.of("--count", "360", "--concurrency", "1"));

        verify(facadeService, times(360)).sendEvent(any());
    }

    @Test
    public void sendsEventsFromS3Successfully() throws URISyntaxException, IOException {
        String record = getCsvFile();
        when(facadeService.sendEvent(any()))
                .thenReturn(ResponseData.builder().build());
        when(appConfig.isEventFileLocal()).thenReturn(false);
        when(appConfig.getS3BasePath()).thenReturn("test");
        when(appConfig.getFileKey()).thenReturn("test");

        final ByteArrayInputStream inRecords = new ByteArrayInputStream(record.getBytes());
        final S3ObjectInputStream s3ObjectInputStreamMock = new S3ObjectInputStream(inRecords, null);

        final S3Object s3ObjectMock = Mockito.mock(S3Object.class);
        when(s3ObjectMock.getObjectContent()).thenReturn(s3ObjectInputStreamMock);
        when(amazonS3.getObject(anyString(), anyString())).thenReturn(s3ObjectMock);
        eventSenderService.runCommand(List.of("--count", "359", "--concurrency", "1"));

        verify(facadeService, times(359)).sendEvent(any());
    }

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
    public void sendsEventsExceptionally() {
        when(facadeService.sendEvent(any()))
                .thenThrow(new RuntimeException("test"));
        when(appConfig.isEventFileLocal()).thenReturn(true);
        when(appConfig.getLocalBasePath()).thenReturn("src/test/resources");
        when(appConfig.getFileKey()).thenReturn("test_events.csv");

        final PerformanceStats performanceStats = eventSenderService
                .sendEvents(80L, 1L);
        assertThat(performanceStats).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(PerformanceStats.builder()
                        .failedRequests(80L)
                        .successfulRequests(0L)
                        .build());

        verify(facadeService, times(80)).sendEvent(any());
    }

    public String getCsvFile() throws URISyntaxException, IOException {
        return String.join("\n",
                Files.readAllLines(Path.of(this.getClass().getClassLoader()
                        .getResource("test_events.csv").toURI())));
    }

    @AfterMethod
    public void teardown() {
        reset(facadeService, sessionCheckService, amazonS3, appConfig);
    }
}

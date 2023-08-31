package com.pauldaniv.promotion.yellowtaxi.client.service.cmd;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CmdResolverTest {

    @Mock
    private Map<String, CmdService> serviceMap;

    private CmdResolver cmdResolver;


    @BeforeTest
    public void setup() {
        MockitoAnnotations.openMocks(this);
        cmdResolver = new CmdResolver(serviceMap);
    }

    @Test
    public void resolvesHandlerSuccessfully() {
        final CmdService mockService = mock(CmdService.class);
        when(serviceMap.get("test")).thenReturn(mockService);

        cmdResolver.handle("test", "--count", "29", "--concurrency", "10");
        verify(mockService).runCommand(List.of("--count", "29", "--concurrency", "10"));
    }

    @Test
    public void failsToResolveHandler() {
        final CmdService mockService = mock(CmdService.class);
        when(serviceMap.get("test")).thenReturn(mockService);

        assertThatThrownBy(() -> cmdResolver
                .handle("test2", "--count", "29", "--concurrency", "10"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unsupported command");
    }

    @AfterTest
    public void teardown() {
        reset(serviceMap);
    }
}

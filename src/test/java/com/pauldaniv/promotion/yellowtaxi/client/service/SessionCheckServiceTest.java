package com.pauldaniv.promotion.yellowtaxi.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SessionCheckServiceTest extends AbstractTestNGSpringContextTests {

    @MockBean
    private FacadeService facadeService;

    @Autowired
    private SessionCheckService sessionCheckService;

    @Test
    public void checkSessionIsActive() {
        when(facadeService.isSessionActive()).thenReturn(true);

        sessionCheckService.isSessionActive();
    }

    @Test
    public void checkSessionIsNotActive() {
        when(facadeService.isSessionActive()).thenReturn(false);

        assertThatThrownBy(() -> sessionCheckService.isSessionActive())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No active session detected");
    }

    @AfterMethod
    public void teardown() {
        reset(facadeService);
    }
}

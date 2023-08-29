package com.pauldaniv.promotion.yellowtaxi.client.service;

import com.pauldaniv.promotion.yellowtaxi.facade.model.AuthResponse;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class LoginServiceTest {

    @Mock
    private FacadeService facadeService;
    @Mock
    private CommandLineReader commandLineReader;

    private LoginService loginService;

    @BeforeTest
    public void setup() {
        MockitoAnnotations.openMocks(this);
        loginService = new LoginService(facadeService, commandLineReader);
    }

    @Test
    public void logsInSuccessfully() {
        when(facadeService.login(any()))
                .thenReturn(new AuthResponse("test", "test.token"));
        when(commandLineReader.read()).thenReturn("test_pass");
        loginService.runCommand(List.of("--username", "test"));
    }


    @Test
    public void logsInEmptyResponseToken() {
        when(facadeService.login(any()))
                .thenReturn(new AuthResponse("test", null));
        when(commandLineReader.read()).thenReturn("test");
        assertThatThrownBy(() -> loginService.runCommand(List.of("--username", "test")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unable to store access token");

    }

    @AfterTest
    public void teardown() {
        reset(facadeService, commandLineReader);
    }
}

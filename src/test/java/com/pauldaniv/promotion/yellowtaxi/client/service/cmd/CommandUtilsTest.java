package com.pauldaniv.promotion.yellowtaxi.client.service.cmd;

import com.pauldaniv.promotion.yellowtaxi.client.model.CommandSpec;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CommandUtilsTest {

    @Test
    public void loadsClassSuccessfully() {
        log.info("Log Class: {}", new CommandUtils());
    }

    @Test
    public void validatesCommandsSuccessfully() {
        CommandUtils.validate(
                List.of("--test", "value"),
                Map.of("--test", new CommandSpec()));
    }

    @Test
    public void failsValidationWithUnsupportedCommand() {
        assertThatThrownBy(() -> CommandUtils.validate(
                List.of("--another", "value"),
                Map.of("--test", new CommandSpec())))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unsupported command");
    }

    @Test
    public void failsValidationWithParametersCount() {
        assertThatThrownBy(() -> CommandUtils.validate(
                List.of("--test", "value", "must_fail"),
                Map.of("--test", new CommandSpec())))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid parameters count");
    }

    @Test
    public void failsValidationWithEmptyParameters() {
        assertThatThrownBy(() -> CommandUtils.validate(
                List.of(),
                Map.of("--test", new CommandSpec())))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No parameters specified");
    }
}

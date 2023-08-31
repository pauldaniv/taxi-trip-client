package com.pauldaniv.promotion.yellowtaxi.client.service.cmd;

import com.pauldaniv.promotion.yellowtaxi.client.model.CommandSpec;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CommandUtils {

    public static Map<String, String> listToMap(final List<String> commands) {
        Map<String, String> commandMap = new HashMap<>();
        for (int i = 0; i < commands.size(); i++) {
            commandMap.put(commands.get(i), commands.get(++i));
        }
        return commandMap;
    }

    public static void validate(final List<String> params,
                                final Map<String, CommandSpec> commands) {
        final List<String> commandsPassed = params.stream()
                .filter(commands.keySet()::contains)
                .toList();
        final List<String> availableCommands = commands.keySet().stream().map(it -> String.format("%s <value>", it)).toList();
        if (params.isEmpty()) {
            final String msg = "No parameters specified";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        if (commandsPassed.isEmpty()) {
            log.error("Unsupported command: {}", params.get(0));
            log.info("List of supported commands: {}", availableCommands);
            throw new RuntimeException("Unsupported command");
        }
        if (params.size() % 2 != 0) {
            log.error("Invalid parameters specified. Must be <key> <value> pattern. Got: {}", params);
            throw new RuntimeException("Invalid parameters count");
        }
    }
}

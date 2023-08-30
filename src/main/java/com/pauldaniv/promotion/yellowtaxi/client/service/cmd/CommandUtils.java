package com.pauldaniv.promotion.yellowtaxi.client.service.cmd;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CommandUtils {
    public static Map<String, String> listToMap(final List<String> commands) {
        Map<String, String> commandMap = new HashMap<>();
        for (int i = 0; i < commands.size() ; i++) {
            commandMap.put(commands.get(i), commands.get(++i));
        }
        return commandMap;
    }

    public static void validate(List<String> params, List<String> commandsPassed, List<String> availableCommands) {
        if (!params.isEmpty() && commandsPassed.isEmpty()) {
            log.error("Unsupported command: {}", params.get(0));
            log.info("List of supported commands: {}", availableCommands);
            throw new RuntimeException();
        }
        if (params.size() % 2 != 0) {
            log.error("Invalid parameters specified. Must be <key> <value> pattern. Got: {}", params);
            throw new RuntimeException();
        }
    }
}

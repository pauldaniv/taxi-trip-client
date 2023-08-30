package com.pauldaniv.promotion.yellowtaxi.client.service.cmd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CmdResolver {

    private final Map<String, CmdService> serviceMap;

    public void handle(String... args) {
        final List<String> commands = List.of(args);
        final String command = commands.get(0);

        Optional.ofNullable(serviceMap.get(command))
                .orElseThrow(() -> {
                    log.error("Unsupported command: {}", command);
                    return new RuntimeException("Unsupported command");
                }).runCommand(commands.subList(1, commands.size()));
    }
}

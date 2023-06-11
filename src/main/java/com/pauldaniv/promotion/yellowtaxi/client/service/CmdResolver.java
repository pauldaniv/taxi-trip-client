package com.pauldaniv.promotion.yellowtaxi.client.service;

import com.pauldaniv.promotion.yellowtaxi.client.model.RunEventParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CmdResolver {

    private final Map<String, CmdService> serviceMap;

    public void handle(String... args) {
        final List<String> commands = List.of(args);
        final String command = commands.get(0);

        serviceMap.get(command).runCommand(commands.subList(1, commands.size()));
    }
}

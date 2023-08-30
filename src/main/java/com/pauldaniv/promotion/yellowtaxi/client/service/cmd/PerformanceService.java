package com.pauldaniv.promotion.yellowtaxi.client.service.cmd;

import com.pauldaniv.promotion.yellowtaxi.client.model.CommandSpec;
import com.pauldaniv.promotion.yellowtaxi.client.model.PerformanceStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.pauldaniv.promotion.yellowtaxi.client.service.cmd.CommandUtils.listToMap;
import static com.pauldaniv.promotion.yellowtaxi.client.service.cmd.CommandUtils.validate;

@Slf4j
@Service("performance_check")
@RequiredArgsConstructor
public class PerformanceService implements CmdService {

    public static final String COUNT = "--count";
    public static final String CONCURRENCY = "--concurrency";
    public static final String TARGET_AVG = "--target-avg";

    private static final Map<String, CommandSpec> COMMANDS = Map.of(
            COUNT, CommandSpec.builder()
                    .typeSpec("number")
                    .description("The number of events to send")
                    .defaultValue("1000")
                    .build(),
            CONCURRENCY, CommandSpec.builder()
                    .typeSpec("number")
                    .description("The number of threads to use during event publishing")
                    .defaultValue("5000")
                    .build(),
            TARGET_AVG, CommandSpec.builder()
                    .typeSpec("number")
                    .description("The number of requests to be send per second on average")
                    .defaultValue("70")
                    .build()
    );

    private final EventSenderService eventSenderService;

    @Override
    public void runCommand(List<String> params) {
        final List<String> commandsPassed = params.stream()
                .filter(COMMANDS.keySet()::contains)
                .toList();

        final List<String> availableCommands = COMMANDS.keySet().stream()
                .map(it -> String.format("%s <number>", it)).toList();
        validate(params, commandsPassed, availableCommands);
        final Map<String, String> commands = listToMap(params);
        final Long eventCount = Long.valueOf(commands.getOrDefault(COUNT,
                COMMANDS.get(COUNT).getDefaultValue()));
        final Long concurrency = Long.valueOf(commands.getOrDefault(CONCURRENCY,
                COMMANDS.get(CONCURRENCY).getDefaultValue()));
        final String targetAvg = commands.getOrDefault(TARGET_AVG,
                COMMANDS.get(TARGET_AVG).getDefaultValue());

        log.info("Collecting performance data...");
        final PerformanceStats performanceStats = eventSenderService.sendEvents(eventCount, concurrency);
        log.info("Done collecting performance data!");
        final BigDecimal avgRequestsPerSecond = performanceStats.getAvgRequestsPerSecond();
        final BigDecimal threshold = new BigDecimal(targetAvg);
        log.info("Verifying that average requests per second is greater than: {}", threshold);
        if(avgRequestsPerSecond.compareTo(threshold) < 0) {
            throw new RuntimeException("Performance requirement did not meet");
        }
        log.info("Success!");
    }
}

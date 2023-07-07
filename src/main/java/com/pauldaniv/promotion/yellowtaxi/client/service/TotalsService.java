package com.pauldaniv.promotion.yellowtaxi.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pauldaniv.promotion.yellowtaxi.client.model.CommandSpec;
import com.pauldaniv.promotion.yellowtaxi.facade.model.TotalsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.pauldaniv.promotion.yellowtaxi.client.service.CommandUtils.listToMap;
import static com.pauldaniv.promotion.yellowtaxi.client.service.CommandUtils.validate;

@Slf4j
@Service("totals")
@RequiredArgsConstructor
public class TotalsService implements CmdService {

    public static final String DAY = "--day";
    public static final String MONTH = "--month";
    public static final String YEAR = "--year";

    private static final Map<String, CommandSpec> COMMANDS = Map.of(
            DAY, CommandSpec.builder()
                    .typeSpec("number")
                    .description("Day of the totals range")
                    .build(),
            MONTH, CommandSpec.builder()
                    .typeSpec("number or month name ")
                    .description("Month of the totals range")
                    .build(),
            YEAR, CommandSpec.builder()
                    .typeSpec("number")
                    .description("Year of the totals range")
                    .defaultValue(String.valueOf(LocalDate.now().getYear()))
                    .build()
    );

    private final FacadeService facadeService;
    private final ObjectMapper objectMapper;

    @Override
    public void runCommand(List<String> params) {

        final List<String> commandsPassed = params.stream()
                .filter(COMMANDS.keySet()::contains)
                .toList();

        final List<String> availableCommands = COMMANDS.keySet().stream().map(it -> String.format("%s <value>", it)).toList();
        validate(params, commandsPassed, availableCommands);
        final Map<String, String> commands = listToMap(params);
        final Integer year = Integer.valueOf(commands.getOrDefault(YEAR, COMMANDS.get(YEAR).getDefaultValue()));
        final Optional<Integer> month = Optional.ofNullable(commands.get(MONTH)).map(Integer::valueOf);
        final Optional<Integer> day = Optional.ofNullable(commands.get(DAY)).map(Integer::valueOf);
        displayTotals(year, month, day);
    }

    private void displayTotals(final Integer year,
                               final Optional<Integer> month,
                               final Optional<Integer> day) {
        log.info("Querying totals with year={}, month={}, day={}", year, month, day);
        final TotalsResponse totals = facadeService.getTotals(year, month.orElse(null), day.orElse(null));
        try {
            final String totalsStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(totals);

            log.info("Totals response: {}", totalsStr);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse json. Message={}", e.getMessage(), e);
        }
    }
}

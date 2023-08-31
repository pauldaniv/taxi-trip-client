package com.pauldaniv.promotion.yellowtaxi.client.service.cmd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pauldaniv.promotion.yellowtaxi.client.model.CommandSpec;
import com.pauldaniv.promotion.yellowtaxi.client.service.FacadeService;
import com.pauldaniv.promotion.yellowtaxi.client.service.SessionCheckService;
import com.pauldaniv.promotion.yellowtaxi.facade.model.TotalsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.pauldaniv.promotion.yellowtaxi.client.service.cmd.CommandUtils.listToMap;
import static com.pauldaniv.promotion.yellowtaxi.client.service.cmd.CommandUtils.validate;

@Slf4j
@Service("totals")
@RequiredArgsConstructor
public class TotalsService implements CmdService {

    public static final String DAY = "--day";
    public static final String MONTH = "--month";
    public static final String YEAR = "--year";

    private static final Map<String, CommandSpec> OPTIONS = Map.of(
            DAY, CommandSpec.builder()
                    .typeSpec("number")
                    .description("Day of the totals range")
                    .defaultValue("2018")
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
    private final SessionCheckService sessionCheckService;
    private final ObjectMapper objectMapper;

    @Override
    public void runCommand(List<String> params) {
        validate(params, OPTIONS);
        final Map<String, String> commands = listToMap(params);
        final Integer year = Integer.valueOf(commands.getOrDefault(YEAR, OPTIONS.get(YEAR).getDefaultValue()));
        final Integer month = Optional.ofNullable(commands.get(MONTH)).map(Integer::valueOf)
                .orElseThrow(() -> new RuntimeException("Month is required!"));
        final Optional<Integer> day = Optional.ofNullable(commands.get(DAY)).map(Integer::valueOf);
        sessionCheckService.isSessionActive();
        displayTotals(year, month, day);
    }

    private void displayTotals(final Integer year,
                               final Integer month,
                               final Optional<Integer> day) {
        log.info("Querying totals with year={}, month={}, day={}", year, month, day);
        final TotalsResponse totals = facadeService.getTotals(year, month, day.orElse(null));
        try {
            final String totalsStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(totals);

            log.info("Totals response: {}", totalsStr);
        } catch (Exception e) {
            log.error("Failed to parse json. Message={}", e.getMessage(), e);
        }
    }
}

package com.pauldaniv.promotion.yellowtaxi.client.service;

import com.pauldaniv.promotion.yellowtaxi.model.TripRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.pauldaniv.promotion.yellowtaxi.client.service.CommandUtils.listToMap;
import static com.pauldaniv.promotion.yellowtaxi.client.service.CommandUtils.validate;

@Slf4j
@Service("event")
@RequiredArgsConstructor
public class EventSenderService implements CmdService {
    public static final String COUNT = "--count";
    public static final String CONCURRENCY = "--concurrency";
    private static final List<String> COMMANDS = List.of(COUNT, CONCURRENCY);

    @Value("${event.file.basePath}")
    private String basePath;

    private final FacadeService facadeService;

    @Override
    public void runCommand(List<String> params) {
        final List<String> commandsPassed = params.stream()
                .filter(COMMANDS::contains)
                .toList();
        final List<String> availableCommands = COMMANDS.stream().map(it -> String.format("%s <number>", it)).toList();
        validate(params, commandsPassed, availableCommands);
        final Map<String, Long> commands = listToMap(params);
        final Long eventCount = commands.getOrDefault(COUNT, 1000L);
        final Long concurrency = commands.getOrDefault(CONCURRENCY, 5L);
        log.info("Using event count: {}, and concurrency: {}", eventCount, concurrency);
        sendEvents(eventCount, concurrency);
    }

    private void sendEvents(final Long count, final Long concurrency) {

        Iterable<CSVRecord> records;
        try (Reader in = new FileReader(String.format("%s/%s", basePath, "2018_Yellow_Taxi_Trip_Data.csv"))) {
            final String[] header = List.of("VendorID", "tpep_pickup_datetime", "tpep_dropoff_datetime", "passenger_count",
                    "trip_distance", "RatecodeID", "store_and_fwd_flag", "PULocationID", "DOLocationID",
                    "payment_type", "fare_amount", "extra", "mta_tax", "tip_amount", "tolls_amount",
                    "improvement_surcharge", "total_amount").toArray(new String[]{});

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(header)
                    .setSkipHeaderRecord(true)
                    .build();

            records = csvFormat.parse(in);
            final ExecutorService executor = Executors.newFixedThreadPool(Math.toIntExact(concurrency));

            long recordCount = 0L;
            for (CSVRecord record : records) {
                recordCount++;
                final TripRequest event = makeEvent(record);
                if (recordCount >= count) {
                    break;
                }
                CompletableFuture.supplyAsync(() -> {
                    log.info("Sending event: {}", event);
                    return facadeService.sendEvent(event);
                }, executor).thenAcceptAsync(it -> {
                    log.info("Processed successfully, status: {}", it);
                }).exceptionallyAsync(it -> {
                    log.error("Failed to send event. Reason: {}", it.getMessage());
                    return null;
                });
            }
            executor.shutdown();
        } catch (IOException e) {
            log.error("Failed to read file: {}", e.getMessage(), e);
        }
    }

    private static TripRequest makeEvent(CSVRecord record) {
        final LocalDateTime tpepPickupDatetime = LocalDateTime.parse(record.get("tpep_pickup_datetime"), DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a"));
        final LocalDateTime tpepDropoffDatetime = LocalDateTime.parse(record.get("tpep_dropoff_datetime"), DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a"));
        return TripRequest.builder()
                .vendorId(Long.valueOf(record.get("VendorID")))
                .tPepPickupDatetime(tpepPickupDatetime)
                .tPepDropOffDatetime(tpepDropoffDatetime)
                .passengerCount(Integer.valueOf(record.get("passenger_count")))
                .tripDistance(Double.valueOf(record.get("trip_distance")))
                .rateCodeId(Long.valueOf(record.get("RatecodeID")))
                .storeAndFwdFlag("Y".equals(record.get("store_and_fwd_flag")))
                .puLocationId(Integer.valueOf(record.get("PULocationID")))
                .doLocationId(Integer.valueOf(record.get("DOLocationID")))
                .paymentTypeId(Long.valueOf(record.get("payment_type")))
                .fareAmount(new BigDecimal(record.get("fare_amount")))
                .extra(new BigDecimal(record.get("extra")))
                .mtaTax(new BigDecimal(record.get("mta_tax")))
                .tipAmount(new BigDecimal(record.get("tip_amount")))
                .tollsAmount(new BigDecimal(record.get("tolls_amount")))
                .improvementSurcharge(new BigDecimal(record.get("improvement_surcharge")))
                .totalAmount(new BigDecimal(record.get("total_amount")))
                .build();
    }
}

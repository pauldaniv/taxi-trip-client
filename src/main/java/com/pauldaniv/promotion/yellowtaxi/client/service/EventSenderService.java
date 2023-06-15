package com.pauldaniv.promotion.yellowtaxi.client.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.pauldaniv.promotion.yellowtaxi.client.model.CommandSpec;
import com.pauldaniv.promotion.yellowtaxi.model.TripRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.pauldaniv.promotion.yellowtaxi.client.service.CommandUtils.listToMap;
import static com.pauldaniv.promotion.yellowtaxi.client.service.CommandUtils.validate;

@Slf4j
@Service("event")
@RequiredArgsConstructor
public class EventSenderService implements CmdService {

    public static final String COUNT = "--count";
    public static final String CONCURRENCY = "--concurrency";

    private static final Map<String, CommandSpec> COMMANDS = Map.of(
            COUNT, CommandSpec.builder()
                    .typeSpec("number")
                    .description("The number of events to send")
                    .defaultValue("1000")
                    .build(),
            CONCURRENCY, CommandSpec.builder()
                    .typeSpec("number")
                    .description("The number of threads to use during event publishing")
                    .defaultValue("10")
                    .build()
    );

    @Value("${event.file.key}")
    private String fileKey;
    @Value("${event.file.localBasePath}")
    private String localBasePath;
    @Value("${event.file.s3BasePath}")
    private String s3BasePath;
    @Value("${event.file.isLocal}")
    private Boolean eventFileLocal;

    private final FacadeService facadeService;
    private final AmazonS3 amazonS3;

    @Override
    public void runCommand(List<String> params) {
        final List<String> commandsPassed = params.stream()
                .filter(COMMANDS.keySet()::contains)
                .toList();
        final List<String> availableCommands = COMMANDS.keySet().stream().map(it -> String.format("%s <number>", it)).toList();
        validate(params, commandsPassed, availableCommands);
        final Map<String, String> commands = listToMap(params);
        final Long eventCount = Long.valueOf(commands.getOrDefault(COUNT,
                COMMANDS.get(COUNT).getDefaultValue()));
        final Long concurrency = Long.valueOf(commands.getOrDefault(CONCURRENCY,
                COMMANDS.get(CONCURRENCY).getDefaultValue()));
        log.info("Using event count: {}, and concurrency: {}", eventCount, concurrency);
        final Instant start = Instant.now();
        sendEvents(eventCount, concurrency);
        showTimeElapsed(eventCount, start);
    }

    private void sendEvents(final Long count, final Long concurrency) {

        Iterable<CSVRecord> records;

        try (final InputStream in = getFileContent();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            final String[] header = List.of("VendorID", "tpep_pickup_datetime", "tpep_dropoff_datetime", "passenger_count",
                    "trip_distance", "RatecodeID", "store_and_fwd_flag", "PULocationID", "DOLocationID",
                    "payment_type", "fare_amount", "extra", "mta_tax", "tip_amount", "tolls_amount",
                    "improvement_surcharge", "total_amount").toArray(new String[]{});

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(header)
                    .setSkipHeaderRecord(true)
                    .build();

            records = csvFormat.parse(reader);
            final ExecutorService executor = Executors.newFixedThreadPool(Math.toIntExact(concurrency));

            long recordCount = 0L;
            for (
                    CSVRecord record : records) {
                recordCount++;
                final TripRequest event = makeEvent(record);
                if (recordCount > count) {
                    if (in instanceof S3ObjectInputStream s3In) {
                        s3In.abort();
                    }
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
            final boolean terminated = executor.awaitTermination(35, TimeUnit.SECONDS);
            if (terminated) {
                log.info("Successfully completed task");
            } else {
                log.warn("Failed to complete some tasks before termination");
            }
        } catch (IOException e) {
            log.error("Failed to read file: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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

    private InputStream getFileContent() throws IOException {
        if (this.eventFileLocal) {
            return Files.newInputStream(Paths.get(String.format("%s/%s", localBasePath, fileKey)));
        } else {
            return amazonS3.getObject(s3BasePath, fileKey).getObjectContent();
        }
    }

    private static void showTimeElapsed(Long eventCount, Instant start) {
        final Instant end = Instant.now();
        final Duration took = Duration.between(start, end);
        log.info("Run completed: Millis took: {}", took.toMillis());
        final BigDecimal secondsTook = new BigDecimal(took.toMillis())
                .divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
        log.info("Run completed: Seconds took: {}", secondsTook);
        log.info("Total events send: {}, Average requests per second: {}",
                eventCount, new BigDecimal(eventCount).divide(secondsTook, 2, RoundingMode.HALF_UP));
    }
}

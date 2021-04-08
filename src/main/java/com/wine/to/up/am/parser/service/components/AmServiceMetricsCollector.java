package com.wine.to.up.am.parser.service.components;

import com.wine.to.up.commonlib.metrics.CommonMetricsCollector;
import io.micrometer.core.instrument.Metrics;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This Class expose methods for recording specific metrics
 * It changes metrics of Micrometer and Prometheus simultaneously
 * Micrometer's metrics exposed at /actuator/prometheus
 * Prometheus' metrics exposed at /metrics-prometheus
 */

@Component
public class AmServiceMetricsCollector extends CommonMetricsCollector {

    private static final String SERVICE_NAME = "am_parser_service";

    private static final String PARSING_STARTED = "parsing_started_total";
    private static final String PARSING_COMPLETE = "parsing_complete_total";
    private static final String PARSING_IN_PROGRESS = "parsing_in_progress";
    private static final String PARSING_DURATION = "parsing_process_duration_summary";
    private static final String TIME_SINCE_LAST_PARSING = "time_since_last_succeeded_parsing";
    private static final String WINE_DETAILS_FETCHING_DURATION = "wine_details_fetching_duration_seconds";
    private static final String WINE_PAGE_FETCHING_DURATION = "wine_page_fetching_duration_seconds";
    private static final String WINE_DETAILS_PARSING_DURATION = "wine_details_parsing_duration_seconds";
    private static final String WINE_PAGE_PARSING_DURATION = "wine_page_parsing_duration_seconds";
    private static final String WINES_PUBLISHED_TO_KAFKA = "wines_published_to_kafka_count";

    private static final String PARSING_COMPLETE_STATUS = "status";
    private static final String REGION_LABEL = "city";

    private static final Counter prometheusParsingStartedCounter = Counter.build()
            .name(PARSING_STARTED)
            .help("Total number of parsing processes ever started")
            .labelNames(REGION_LABEL)
            .register();
    private static final Counter prometheusParsingCompleteCounter = Counter.build()
            .name(PARSING_COMPLETE)
            .help("Total number of parsing processes ever completed")
            .labelNames(REGION_LABEL, PARSING_COMPLETE_STATUS)
            .register();
    private static final Gauge prometheusParsingInProgressGauge = Gauge.build()
            .name(PARSING_IN_PROGRESS)
            .help("Total number of parsing processes currently in progress")
            .labelNames(REGION_LABEL)
            .register();
    private static final AtomicInteger micrometerParsingInProgressGauge = Metrics.gauge(PARSING_IN_PROGRESS, new AtomicInteger(0));
    private static final Summary prometheusParsingDurationSummary = Summary.build()
            .name(PARSING_DURATION)
            .help("The duration of every parsing process completed so far")
            .labelNames(REGION_LABEL)
            .register();
    private static final Gauge prometheusTimeSinceLastParsingGauge = Gauge.build()
            .name(TIME_SINCE_LAST_PARSING)
            .help("The amount of time since the last successfully completed parsing process")
            .labelNames(REGION_LABEL)
            .register();
    private static final AtomicLong micrometerTimeSinceLastParsingGauge = Metrics.gauge(TIME_SINCE_LAST_PARSING, new AtomicLong(0));
    private static final Summary prometheusWineDetailsFetchingDurationSummary = Summary.build()
            .name(WINE_DETAILS_FETCHING_DURATION)
            .help("The duration of every fetching of a wine details page")
            .labelNames(REGION_LABEL)
            .register();
    private static final Summary prometheusWinePageFetchingDurationSummary = Summary.build()
            .name(WINE_PAGE_FETCHING_DURATION)
            .help("The duration of every parsing of a wine details page")
            .labelNames(REGION_LABEL)
            .register();
    private static final Summary prometheusWineDetailsParsingDurationSummary = Summary.build()
            .name(WINE_DETAILS_PARSING_DURATION)
            .help("The duration of every parsing of a wine details page")
            .labelNames(REGION_LABEL)
            .register();
    private static final Summary prometheusWinePageParsingDurationSummary = Summary.build()
            .name(WINE_PAGE_PARSING_DURATION)
            .help("The duration of every parsing of a wines page")
            .labelNames(REGION_LABEL)
            .register();
    private static final Counter prometheusWinesPublishedToKafkaCounter = Counter.build()
            .name(WINES_PUBLISHED_TO_KAFKA)
            .help("Number of wines that have been sent to Kafka")
            .labelNames(REGION_LABEL)
            .register();

    private static final String PARSED_WINES_SUCCESS = "parsed_wines_success";
    private static final String WINES_PARSED_UNSUCCESSFUL = "wines_parsed_unsuccessful";
    private static final String IS_BANNED = "is_banned";
    private static final String NUMBER_OF_WINES_CREATED = "number_of_wines_created";
    private static final String NUMBER_OF_WINES_UPDATED = "number_of_wines_updated";
    private static final String NUMBER_OF_WINES_DELETED = "number_of_wines_deleted";
    private static final String PERCENTAGE_OF_UNSUCCESSFULLY_PARSED_WINES = "percentage_of_unsuccessfully_parsed_wines";
    private static final String JOB_EXECUTION_TIME = "job_execution_time";

    private static final Gauge prometheusParsedWinesSuccessGauge = Gauge.build()
            .name(PARSED_WINES_SUCCESS)
            .help("Number of successfully parsed wines")
            .labelNames(REGION_LABEL)
            .register();
    private static final AtomicInteger micrometerParsedWinesSuccessGauge = Metrics.gauge(PARSED_WINES_SUCCESS, new AtomicInteger(0));
    private static final Gauge prometheusWinesParsedUnsuccessfulGauge = Gauge.build()
            .name(WINES_PARSED_UNSUCCESSFUL)
            .help("Number of unsuccessfully parsed wines")
            .labelNames(REGION_LABEL)
            .register();
    private static final AtomicInteger micrometerWinesParsedUnsuccessfulGauge = Metrics.gauge(WINES_PARSED_UNSUCCESSFUL, new AtomicInteger(0));
    private static final Gauge prometheusIsBannedGauge = Gauge.build()
            .name(IS_BANNED)
            .help("Is banned host's IP")
            .labelNames(REGION_LABEL)
            .register();
    private static final AtomicInteger micrometerIsBannedGauge = Metrics.gauge(IS_BANNED, new AtomicInteger(0));
    private static final Counter prometheusNumberOfWinesCreatedCounter = Counter.build()
            .name(NUMBER_OF_WINES_CREATED)
            .help("Number of created wines during 1 parsing")
            .labelNames(REGION_LABEL)
            .register();
    private static final Counter prometheusNumberOfWinesUpdatedCounter = Counter.build()
            .name(NUMBER_OF_WINES_UPDATED)
            .help("Number of updated wines during 1 parsing")
            .labelNames(REGION_LABEL)
            .register();
    private static final Counter prometheusNumberOfWinesDeletedCounter = Counter.build()
            .name(NUMBER_OF_WINES_DELETED)
            .help("Number of deleted wines during 1 parsing (due to irrelevance)")
            .labelNames(REGION_LABEL)
            .register();
    private static final Gauge prometheusPercentageOfUnsuccessfullyParsedWinesGauge = Gauge.build()
            .name(PERCENTAGE_OF_UNSUCCESSFULLY_PARSED_WINES)
            .help("Percent of unsuccessfully parsed wines")
            .labelNames(REGION_LABEL)
            .register();
    private static final AtomicInteger micrometerPercentageOfUnsuccessfullyParsedWinesGauge = Metrics.gauge(PERCENTAGE_OF_UNSUCCESSFULLY_PARSED_WINES, new AtomicInteger(0));
    private static final Summary prometheusJobExecutionTimeSummary = Summary.build()
            .name(JOB_EXECUTION_TIME)
            .help("Job execution time")
            .labelNames(REGION_LABEL)
            .register();

    public AmServiceMetricsCollector() {
        this(SERVICE_NAME);
        countParsingStart(0, "Санкт-Петербург");
        incParsingInProgress(0, "Санкт-Петербург");
        decParsingInProgress(0, "Санкт-Петербург");
        countParsingComplete("success", "Санкт-Петербург", 0);
        countParsingComplete("failed", "Санкт-Петербург", 0);
        countNumberOfWinesCreated(0, "Санкт-Петербург");
        countNumberOfWinesUpdated(0, "Санкт-Петербург");
        countNumberOfWinesDeleted(0, "Санкт-Петербург");
        countWinesPublishedToKafka(0, "Санкт-Петербург");
        percentageOfUnsuccessfullyParsedWines(0, "Санкт-Петербург");
        timeParsingDuration(0, "Санкт-Петербург");
        countTimeSinceLastParsing(0, "Санкт-Петербург");
        timeWineDetailsFetchingDuration(0, "Санкт-Петербург");
        timeWineDetailsParsingDuration(0, "Санкт-Петербург");
        timeWinePageParsingDuration(0, "Санкт-Петербург");
        timeWinePageFetchingDuration(0, "Санкт-Петербург");
        parsedWinesSuccess(0, "Санкт-Петербург");
        winesParsedUnsuccessful(0, "Санкт-Петербург");
        isBanned(0, "Санкт-Петербург");
        jobExecutionTime(0, "Санкт-Петербург");
    }

    private AmServiceMetricsCollector(String serviceName) {
        super(serviceName);
    }

    public void countParsingStart(String region) {
        Metrics.counter(PARSING_STARTED, REGION_LABEL, region).increment();
        prometheusParsingStartedCounter.labels(region).inc();
    }

    public void countParsingStart(double value, String region) {
        Metrics.counter(PARSING_STARTED, REGION_LABEL, region).increment(value);
        prometheusParsingStartedCounter.labels(region).inc(value);
    }

    public void countParsingComplete(String status, String region) {
        Metrics.counter(PARSING_COMPLETE, PARSING_COMPLETE_STATUS, status).increment();
        prometheusParsingCompleteCounter.labels(region, status).inc();
    }

    public void countParsingComplete(String status, String region, double value) {
        Metrics.counter(PARSING_COMPLETE, PARSING_COMPLETE_STATUS, status).increment(value);
        prometheusParsingCompleteCounter.labels(region, status).inc(value);
    }

    public void incParsingInProgress(String region) {
        prometheusParsingInProgressGauge.labels(region).inc();
        micrometerParsingInProgressGauge.getAndIncrement();
    }

    public void incParsingInProgress(double value, String region) {
        prometheusParsingInProgressGauge.labels(region).inc(value);
        micrometerParsingInProgressGauge.getAndAdd((int)value);
    }

    public void decParsingInProgress(String region) {
        prometheusParsingInProgressGauge.labels(region).dec();
        micrometerParsingInProgressGauge.getAndDecrement();
    }

    public void decParsingInProgress(double value, String region) {
        prometheusParsingInProgressGauge.labels(region).dec(value);
        micrometerParsingInProgressGauge.getAndAdd((int)value);
    }

    public void timeParsingDuration(long nanoTime, String region) {
        long secondsTime = TimeUnit.NANOSECONDS.toSeconds(nanoTime);
        prometheusParsingDurationSummary.labels(region).observe(secondsTime);
        Metrics.summary(PARSING_DURATION, REGION_LABEL, region).record(secondsTime);
    }

    public void countTimeSinceLastParsing(long nanoTime, String region) {
        long secondsTime = TimeUnit.NANOSECONDS.toSeconds(nanoTime);
        prometheusTimeSinceLastParsingGauge.labels(region).set(secondsTime);
        micrometerTimeSinceLastParsingGauge.set(secondsTime);
    }

    public void timeWineDetailsFetchingDuration(long nanoTime, String region) {
        long secondsTime = TimeUnit.NANOSECONDS.toSeconds(nanoTime);
        prometheusWineDetailsFetchingDurationSummary.labels(region).observe(secondsTime);
        Metrics.summary(WINE_DETAILS_FETCHING_DURATION, REGION_LABEL, region).record(secondsTime);
    }

    public void timeWinePageFetchingDuration(long nanoTime, String region) {
        long secondsTime = TimeUnit.NANOSECONDS.toSeconds(nanoTime);
        prometheusWinePageFetchingDurationSummary.labels(region).observe(secondsTime);
        Metrics.summary(WINE_PAGE_FETCHING_DURATION, REGION_LABEL, region).record(secondsTime);
    }

    public void timeWineDetailsParsingDuration(long nanoTime, String region) {
        long secondsTime = TimeUnit.NANOSECONDS.toSeconds(nanoTime);
        prometheusWineDetailsParsingDurationSummary.labels(region).observe(secondsTime);
        Metrics.summary(WINE_DETAILS_PARSING_DURATION, REGION_LABEL, region).record(secondsTime);
    }

    public void timeWinePageParsingDuration(long nanoTime, String region) {
        long secondsTime = TimeUnit.NANOSECONDS.toSeconds(nanoTime);
        prometheusWinePageParsingDurationSummary.labels(region).observe(secondsTime);
        Metrics.summary(WINE_PAGE_PARSING_DURATION, REGION_LABEL, region).record(secondsTime);
    }

    public void countWinesPublishedToKafka(double wineNum, String region) {
        Metrics.counter(WINES_PUBLISHED_TO_KAFKA, REGION_LABEL, region).increment(wineNum);
        prometheusWinesPublishedToKafkaCounter.labels(region).inc(wineNum);
    }

    public void parsedWinesSuccess(int countParsedWinesSuccess, String region) {
        prometheusParsedWinesSuccessGauge.labels(region).set(countParsedWinesSuccess);
        micrometerParsedWinesSuccessGauge.set(countParsedWinesSuccess);
    }

    public void winesParsedUnsuccessful(int countParsedWinesUnsuccessful, String region) {
        prometheusWinesParsedUnsuccessfulGauge.labels(region).set(countParsedWinesUnsuccessful);
        micrometerWinesParsedUnsuccessfulGauge.set(countParsedWinesUnsuccessful);
    }

    public void isBanned(int bool, String region) {
        prometheusIsBannedGauge.labels(region).set(bool);
        micrometerIsBannedGauge.set(bool);
    }

    public void countNumberOfWinesCreated(String region) {
        Metrics.counter(NUMBER_OF_WINES_CREATED, REGION_LABEL, region).increment();
        prometheusNumberOfWinesCreatedCounter.labels(region).inc();
    }

    public void countNumberOfWinesCreated(double value, String region) {
        Metrics.counter(NUMBER_OF_WINES_CREATED, REGION_LABEL, region).increment(value);
        prometheusNumberOfWinesCreatedCounter.labels(region).inc(value);
    }

    public void countNumberOfWinesUpdated(String region) {
        Metrics.counter(NUMBER_OF_WINES_UPDATED, REGION_LABEL, region).increment();
        prometheusNumberOfWinesUpdatedCounter.labels(region).inc();
    }

    public void countNumberOfWinesUpdated(double value, String region) {
        Metrics.counter(NUMBER_OF_WINES_UPDATED, REGION_LABEL, region).increment(value);
        prometheusNumberOfWinesUpdatedCounter.labels(region).inc(value);
    }

    public void countNumberOfWinesDeleted(String region) {
        Metrics.counter(NUMBER_OF_WINES_DELETED, REGION_LABEL, region).increment();
        prometheusNumberOfWinesDeletedCounter.labels(region).inc();
    }

    public void countNumberOfWinesDeleted(double value, String region) {
        Metrics.counter(NUMBER_OF_WINES_DELETED, REGION_LABEL, region).increment(value);
        prometheusNumberOfWinesDeletedCounter.labels(region).inc(value);
    }

    public void percentageOfUnsuccessfullyParsedWines(int percentUnsuccessfullyParsedWines, String region){
        prometheusPercentageOfUnsuccessfullyParsedWinesGauge.labels(region).set(percentUnsuccessfullyParsedWines);
        micrometerPercentageOfUnsuccessfullyParsedWinesGauge.set(percentUnsuccessfullyParsedWines);
    }

    public void jobExecutionTime(long nanoTime, String region) {
        long milliTime = TimeUnit.NANOSECONDS.toMillis(nanoTime);
        prometheusJobExecutionTimeSummary.labels(region).observe(milliTime);
        Metrics.summary(JOB_EXECUTION_TIME, REGION_LABEL, region).record(milliTime);
    }
  
}

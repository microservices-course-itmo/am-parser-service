package com.wine.to.up.am.parser.service.components;

import com.wine.to.up.commonlib.metrics.CommonMetricsCollector;
import io.micrometer.core.instrument.Metrics;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static final String WINE_DETAILS_FETCHING_DURATION = "wine_details_fetching_duration";
    private static final String WINE_PAGE_FETCHING_DURATION = "wine_page_fetching_duration";
    private static final String WINE_DETAILS_PARSING_DURATION = "wine_details_parsing_duration";
    private static final String WINE_PAGE_PARSING_DURATION = "wine_page_parsing_duration";
    private static final String WINES_PUBLISHED_TO_KAFKA = "wines_published_to_kafka_count";

    private static final String PARSING_COMPLETE_STATUS = "status";

    private static final Counter prometheusParsingStartedCounter = Counter.build()
            .name(PARSING_STARTED)
            .help("Total number of parsing processes ever started")
            .register();
    private static final Counter prometheusParsingCompleteCounter = Counter.build()
            .name(PARSING_COMPLETE)
            .help("Total number of parsing processes ever completed")
            .labelNames(PARSING_COMPLETE_STATUS)
            .register();
    private static final Gauge prometheusParsingInProgressGauge = Gauge.build()
            .name(PARSING_IN_PROGRESS)
            .help("Total number of parsing processes currently in progress")
            .register();
    private static final Summary prometheusParsingDurationSummary = Summary.build()
            .name(PARSING_DURATION)
            .help("The duration of every parsing process completed so far")
            .register();
    private static final Gauge prometheusTimeSinceLastParsingGauge = Gauge.build()
            .name(TIME_SINCE_LAST_PARSING)
            .help("The amount of time since the last successfully completed parsing process")
            .register();
    private static final Summary prometheusWineDetailsFetchingDurationSummary = Summary.build()
            .name(WINE_DETAILS_FETCHING_DURATION)
            .help("The duration of every fetching of a wine details page")
            .register();
    private static final Summary prometheusWinePageFetchingDurationSummary = Summary.build()
            .name(WINE_PAGE_FETCHING_DURATION)
            .help("The duration of every parsing of a wine details page")
            .register();
    private static final Summary prometheusWineDetailsParsingDurationSummary = Summary.build()
            .name(WINE_DETAILS_PARSING_DURATION)
            .help("The duration of every parsing of a wine details page")
            .register();
    private static final Summary prometheusWinePageParsingDurationSummary = Summary.build()
            .name(WINE_PAGE_PARSING_DURATION)
            .help("The duration of every parsing of a wines page")
            .register();
    private static final Counter prometheusWinesPublishedToKafkaCounter = Counter.build()
            .name(WINES_PUBLISHED_TO_KAFKA)
            .help("Number of wines that have been sent to Kafka")
            .register();

    private static final String PARSED_WINES_SUCCESS = "parsed_wines_success";
    private static final String WINES_PARSED_UNSUCCESSFUL = "wines_parsed_unsuccessful";
    private static final String IS_BANNED = "is_banned";
    private static final String NUMBER_OF_WINES_CREATED = "number_of_wines_created";
    private static final String NUMBER_OF_WINES_UPDATED = "number_of_wines_updated";
    private static final String NUMBER_OF_WINES_DELETED = "number_of_wines_deleted";
    private static final String PERCENTAGE_OF_UNSUCCESSFULLY_PARSED_WINES = "percentage_of_unsuccessfully_parsed_wines";
    private static final String JOB_EXECUTION_TIME = "job_execution_time";

    private static final Gauge parsedWinesSuccessGauge = Gauge.build()
            .name(PARSED_WINES_SUCCESS)
            .help("Number of successfully parsed wines")
            .register();
    private static final Gauge winesParsedUnsuccessfulGauge = Gauge.build()
            .name(WINES_PARSED_UNSUCCESSFUL)
            .help("Number of unsuccessfully parsed wines")
            .register();
    private static final Gauge isBannedGauge = Gauge.build()
            .name(IS_BANNED)
            .help("Is banned host's IP")
            .register();
    private static final Counter numberOfWinesCreatedCounter = Counter.build()
            .name(NUMBER_OF_WINES_CREATED)
            .help("Number of created wines during 1 parsing")
            .register();
    private static final Counter numberOfWinesUpdatedCounter = Counter.build()
            .name(NUMBER_OF_WINES_UPDATED)
            .help("Number of updated wines during 1 parsing")
            .register();
    private static final Counter numberOfWinesDeletedCounter = Counter.build()
            .name(NUMBER_OF_WINES_DELETED)
            .help("Number of deleted wines during 1 parsing (due to irrelevance)")
            .register();
    private static final Gauge percentageOfUnsuccessfullyParsedWinesGauge = Gauge.build()
            .name(PERCENTAGE_OF_UNSUCCESSFULLY_PARSED_WINES)
            .help("Percent of unsuccessfully parsed wines")
            .register();
    private static final Summary jobExecutionTimeSummary = Summary.build()
            .name(JOB_EXECUTION_TIME)
            .help("Job execution time")
            .register();

    public AmServiceMetricsCollector() {
        this(SERVICE_NAME);
    }

    private AmServiceMetricsCollector(String serviceName) {
        super(serviceName);
    }

    public void countParsingStart() {
        Metrics.counter(PARSING_STARTED).increment();
        prometheusParsingStartedCounter.inc();
    }

    public void countParsingComplete(String status) {
        Metrics.counter(PARSING_COMPLETE, PARSING_COMPLETE_STATUS, status).increment();
        prometheusParsingCompleteCounter.labels(status).inc();
    }

    public void incParsingInProgress() {
        prometheusParsingInProgressGauge.inc();
        AtomicInteger gauge = Metrics.gauge(PARSING_IN_PROGRESS, new AtomicInteger(0));
        if(gauge != null) {
            gauge.getAndIncrement();
        }
    }

    public void decParsingInProgress() {
        prometheusParsingInProgressGauge.dec();
        AtomicInteger gauge = Metrics.gauge(PARSING_IN_PROGRESS, new AtomicInteger(0));
        if(gauge != null) {
            gauge.getAndDecrement();
        }
    }

    public void timeParsingDuration(long nanoTime) {
        long milliTime = TimeUnit.NANOSECONDS.toMillis(nanoTime);
        prometheusParsingDurationSummary.observe(milliTime);
        Metrics.summary(PARSING_DURATION).record(milliTime);
    }

    public void countTimeSinceLastParsing(long nanoTime) {
        long milliTime = TimeUnit.NANOSECONDS.toMillis(nanoTime);
        prometheusTimeSinceLastParsingGauge.set(milliTime);
        Metrics.summary(TIME_SINCE_LAST_PARSING).record(milliTime);
    }

    public void timeWineDetailsFetchingDuration(long nanoTime) {
        long milliTime = TimeUnit.NANOSECONDS.toMillis(nanoTime);
        prometheusWineDetailsFetchingDurationSummary.observe(milliTime);
        Metrics.summary(WINE_DETAILS_FETCHING_DURATION).record(milliTime);
    }

    public void timeWinePageFetchingDuration(long nanoTime) {
        long milliTime = TimeUnit.NANOSECONDS.toMillis(nanoTime);
        prometheusWinePageFetchingDurationSummary.observe(milliTime);
        Metrics.summary(WINE_PAGE_FETCHING_DURATION).record(milliTime);
    }

    public void timeWineDetailsParsingDuration(long nanoTime) {
        long milliTime = TimeUnit.NANOSECONDS.toMillis(nanoTime);
        prometheusWineDetailsParsingDurationSummary.observe(milliTime);
        Metrics.summary(WINE_DETAILS_PARSING_DURATION).record(milliTime);
    }

    public void timeWinePageParsingDuration(long nanoTime) {
        long milliTime = TimeUnit.NANOSECONDS.toMillis(nanoTime);
        prometheusWinePageParsingDurationSummary.observe(milliTime);
        Metrics.summary(WINE_PAGE_PARSING_DURATION).record(milliTime);
    }

    public void countWinesPublishedToKafka(double wineNum) {
        Metrics.counter(WINES_PUBLISHED_TO_KAFKA).increment();
        prometheusWinesPublishedToKafkaCounter.inc(wineNum);
    }

    public void parsedWinesSuccess(int countParsedWinesSuccess) {
        Metrics.gauge(PARSED_WINES_SUCCESS, countParsedWinesSuccess);
        parsedWinesSuccessGauge.set(countParsedWinesSuccess);
    }

    public void winesParsedUnsuccessful(int countParsedWinesUnsuccessful) {
        Metrics.gauge(WINES_PARSED_UNSUCCESSFUL, countParsedWinesUnsuccessful);
        winesParsedUnsuccessfulGauge.set(countParsedWinesUnsuccessful);
    }

    public void isBanned(int bool) {
        Metrics.gauge(IS_BANNED, bool);
        isBannedGauge.set(bool);
    }

    public void countNumberOfWinesCreated() {
        Metrics.counter(NUMBER_OF_WINES_CREATED).increment();
        numberOfWinesCreatedCounter.inc();
    }

    public void countNumberOfWinesUpdated() {
        Metrics.counter(NUMBER_OF_WINES_UPDATED).increment();
        numberOfWinesUpdatedCounter.inc();
    }

    public void countNumberOfWinesDeleted() {
        Metrics.counter(NUMBER_OF_WINES_DELETED).increment();
        numberOfWinesDeletedCounter.inc();
    }

    public void percentageOfUnsuccessfullyParsedWines(int percentUnsuccessfullyParsedWines){
        Metrics.gauge(PARSED_WINES_SUCCESS, percentUnsuccessfullyParsedWines);
        percentageOfUnsuccessfullyParsedWinesGauge.set(percentUnsuccessfullyParsedWines);
    }

    public void jobExecutionTime(long nanoTime) {
        long milliTime = TimeUnit.NANOSECONDS.toMillis(nanoTime);
        jobExecutionTimeSummary.observe(milliTime);
        Metrics.summary(JOB_EXECUTION_TIME).record(milliTime);
    }
  
}

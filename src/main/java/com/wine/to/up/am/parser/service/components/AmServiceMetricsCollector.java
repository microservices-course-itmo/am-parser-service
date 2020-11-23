package com.wine.to.up.am.parser.service.components;

import com.wine.to.up.commonlib.metrics.CommonMetricsCollector;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Metrics;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;

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

    private final Counter prometheusParsingStartedCounter;
    private final Counter prometheusParsingCompleteCounter;
    private final Gauge prometheusParsingInProgressGauge;
    private final Summary prometheusParsingDurationSummary;
    private final Gauge prometheusTimeSinceLastParsingGauge;
    private final Summary prometheusWineDetailsFetchingDurationSummary;
    private final Summary prometheusWinePageFetchingDurationSummary;
    private final Summary prometheusWineDetailsParsingDurationSummary;
    private final Summary prometheusWinePageParsingDurationSummary;
    private final Counter prometheusWinesPublishedToKafkaCounter;

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

    public AmServiceMetricsCollector() {
        this(SERVICE_NAME);
    }

    private AmServiceMetricsCollector(String serviceName) {
        super(serviceName);
        this.prometheusParsingStartedCounter = createAndRegisterCounter(
                PARSING_STARTED,
                "Total number of parsing processes ever started"
        );

        this.prometheusParsingCompleteCounter = createAndRegisterCounterLabeled(
                PARSING_COMPLETE,
                "Total number of parsing processes ever completed",
                PARSING_COMPLETE_STATUS
        );
        this.prometheusParsingInProgressGauge = createAndRegisterGauge(
                PARSING_IN_PROGRESS,
                "Total number of parsing processes currently in progress"
        );
        this.prometheusParsingDurationSummary = createAndRegisterSummary(
                PARSING_DURATION,
                "The duration of every parsing process completed so far"
        );
        this.prometheusTimeSinceLastParsingGauge = createAndRegisterGauge(
                TIME_SINCE_LAST_PARSING,
                "The amount of time since the last successfully completed parsing process"
        );
        this.prometheusWineDetailsFetchingDurationSummary = createAndRegisterSummary(
                WINE_DETAILS_FETCHING_DURATION,
                "The duration of every fetching of a wine details page"
        );
        this.prometheusWinePageFetchingDurationSummary = createAndRegisterSummary(
                WINE_PAGE_FETCHING_DURATION,
                "The duration of every fetching of a wines page"
        );
        this.prometheusWineDetailsParsingDurationSummary = createAndRegisterSummary(
                WINE_DETAILS_PARSING_DURATION,
                "The duration of every parsing of a wine details page"
        );
        this.prometheusWinePageParsingDurationSummary = createAndRegisterSummary(
                WINE_PAGE_PARSING_DURATION,
                "The duration of every parsing of a wines page"
        );
        this.prometheusWinesPublishedToKafkaCounter = createAndRegisterCounter(
                WINES_PUBLISHED_TO_KAFKA,
                "Number of wines that have been sent to Kafka"
        );
    }

    private Counter createAndRegisterCounter(String name, String help) {
        return Counter.build()
                .namespace(SERVICE_NAME)
                .name(name)
                .help(help)
                .register();
    }

    private Counter createAndRegisterCounterLabeled(String name, String help, String... labels) {
        return Counter.build()
                .namespace(SERVICE_NAME)
                .name(name)
                .help(help)
                .labelNames(labels)
                .register();
    }

    private Gauge createAndRegisterGauge(String name, String help) {
        return Gauge.build()
                .namespace(SERVICE_NAME)
                .name(name)
                .help(help)
                .register();
    }

    private Summary createAndRegisterSummary(String name, String help) {
        return Summary.build()
                .namespace(SERVICE_NAME)
                .name(name)
                .help(help)
                .register();
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

}

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
            .namespace(SERVICE_NAME)
            .name(PARSING_STARTED)
            .help("Total number of parsing processes ever started")
            .register();
    private static final Counter prometheusParsingCompleteCounter = Counter.build()
            .namespace(SERVICE_NAME)
            .name(PARSING_COMPLETE)
            .help("Total number of parsing processes ever completed")
            .labelNames(PARSING_COMPLETE_STATUS)
            .register();
    private static final Gauge prometheusParsingInProgressGauge = Gauge.build()
            .namespace(SERVICE_NAME)
            .name(PARSING_IN_PROGRESS)
            .help("Total number of parsing processes currently in progress")
            .register();
    private static final Summary prometheusParsingDurationSummary = Summary.build()
            .namespace(SERVICE_NAME)
            .name(PARSING_DURATION)
            .help("The duration of every parsing process completed so far")
            .register();
    private static final Gauge prometheusTimeSinceLastParsingGauge = Gauge.build()
            .namespace(SERVICE_NAME)
            .name(TIME_SINCE_LAST_PARSING)
            .help("The amount of time since the last successfully completed parsing process")
            .register();
    private static final Summary prometheusWineDetailsFetchingDurationSummary = Summary.build()
            .namespace(SERVICE_NAME)
            .name(WINE_DETAILS_FETCHING_DURATION)
            .help("The duration of every fetching of a wine details page")
            .register();
    private static final Summary prometheusWinePageFetchingDurationSummary = Summary.build()
            .namespace(SERVICE_NAME)
            .name(WINE_PAGE_FETCHING_DURATION)
            .help("The duration of every parsing of a wine details page")
            .register();
    private static final Summary prometheusWineDetailsParsingDurationSummary = Summary.build()
            .namespace(SERVICE_NAME)
            .name(WINE_DETAILS_PARSING_DURATION)
            .help("The duration of every parsing of a wine details page")
            .register();
    private static final Summary prometheusWinePageParsingDurationSummary = Summary.build()
            .namespace(SERVICE_NAME)
            .name(WINE_PAGE_PARSING_DURATION)
            .help("The duration of every parsing of a wines page")
            .register();
    private static final Counter prometheusWinesPublishedToKafkaCounter = Counter.build()
            .namespace(SERVICE_NAME)
            .name(WINES_PUBLISHED_TO_KAFKA)
            .help("Number of wines that have been sent to Kafka")
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

}

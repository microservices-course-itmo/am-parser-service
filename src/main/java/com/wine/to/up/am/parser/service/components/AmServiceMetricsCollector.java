package com.wine.to.up.am.parser.service.components;

import com.wine.to.up.commonlib.metrics.CommonMetricsCollector;
import io.micrometer.core.instrument.Metrics;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * This Class expose methods for recording specific metrics
 * It changes metrics of Micrometer and Prometheus simultaneously
 * Micrometer's metrics exposed at /actuator/prometheus
 * Prometheus' metrics exposed at /metrics-prometheus
 */

@Component
public class AmServiceMetricsCollector extends CommonMetricsCollector {

    private static final String SERVICE_NAME = "am_parser_service";

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

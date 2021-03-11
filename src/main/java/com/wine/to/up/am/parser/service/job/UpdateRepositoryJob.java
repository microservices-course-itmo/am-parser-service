package com.wine.to.up.am.parser.service.job;

import com.wine.to.up.am.parser.service.components.AmServiceMetricsCollector;
import com.wine.to.up.am.parser.service.service.UpdateService;
import com.wine.to.up.am.parser.service.util.TrackExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Job для периодического обновления данных о винах
 *
 * @author Филимонов Олег
 */

@Component
@Slf4j
public class UpdateRepositoryJob {

    @Resource
    private UpdateService updateService;

    private final AmServiceMetricsCollector metricsCollector;

    public UpdateRepositoryJob(AmServiceMetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    /**
     * Каждый день обновляет список вин
     */
    @Scheduled(cron = "${job.cron.update.repository}")
    @TrackExecutionTime
    public void runJob() {
        long jobStart = System.nanoTime();
        updateService.updateDictionary();
        updateService.updateWines();
        long jobEnd = System.nanoTime();
        metricsCollector.jobExecutionTime(jobEnd - jobStart);
    }
}

package com.wine.to.up.am.parser.service.job;

import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Job для периодического обновления данных о винах
 *
 * @author Филимонов Олег
 */

@Component
@Slf4j
public class UpdateRepositoryJob {

    @Autowired
    @Qualifier("updateServiceImpl")
    private UpdateService updateService;

    /**
     * Каждую неделю обновляет список вин
     */
    @Scheduled(cron = "${job.cron.update.repository}")
    public void runJob() {
        long startDate = new Date().getTime();
        log.info("start ActualizeWineJob run job method at " + startDate);
        updateService.updateDictionary();
        updateService.updateWines();
        log.info("end ActualizeWineJob run job method at " + new Date().getTime() + " duration = " + (new Date().getTime() - startDate));
    }

}

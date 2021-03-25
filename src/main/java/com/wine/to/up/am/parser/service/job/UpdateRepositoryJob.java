package com.wine.to.up.am.parser.service.job;

import com.wine.to.up.am.parser.service.components.AmServiceMetricsCollector;
import com.wine.to.up.am.parser.service.domain.entity.Wine;
import com.wine.to.up.am.parser.service.logging.AmServiceNotableEvents;
import com.wine.to.up.am.parser.service.model.dto.AdditionalProps;
import com.wine.to.up.am.parser.service.model.dto.AmWine;
import com.wine.to.up.am.parser.service.repository.WineRepository;
import com.wine.to.up.am.parser.service.service.AmClient;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.SearchService;
import com.wine.to.up.am.parser.service.service.UpdateService;
import com.wine.to.up.am.parser.service.util.log.TrackExecutionTime;
import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Job для периодического обновления данных о винах
 *
 * @author Филимонов Олег
 */

@Component
@Slf4j
public class UpdateRepositoryJob {

    @InjectEventLogger
    private EventLogger eventLogger;

    @Resource
    private UpdateService updateService;

    private static AtomicLong pageCursor = new AtomicLong(1L);

    private static AtomicInteger proprsCursor = new AtomicInteger(1);

    private final AmServiceMetricsCollector metricsCollector;

    private final AmClient amClient;

    private final AmService amService;

    private final SearchService searchService;

    private final WineRepository wineRepository;

    public UpdateRepositoryJob(AmServiceMetricsCollector metricsCollector,
                               AmClient amClient,
                               AmService amService,
                               UpdateService updateService,
                               SearchService searchService,
                               WineRepository wineRepository) {
        this.metricsCollector = metricsCollector;
        this.amClient = amClient;
        this.amService = amService;
        this.searchService = searchService;
        this.wineRepository = wineRepository;
    }

    /**
     * Каждый день обновляет список вин
     */
    @Scheduled(cron = "${job.cron.update.repository}")
    public void runJob() {
        ZoneOffset zone = ZoneOffset.of("Z");
        LocalDateTime startDate = LocalDateTime.now();
        long jobStart = System.nanoTime();
        log.info("start ActualizeWineJob run job method at " + startDate);
        updateService.updateDictionary();
        updateService.updateWines();
        LocalDateTime endDate = LocalDateTime.now();
        long jobEnd = System.nanoTime();
        metricsCollector.jobExecutionTime(jobEnd - jobStart);
        log.info("end ActualizeWineJob run job method at " + endDate + " duration = " + (endDate.toEpochSecond(zone) - startDate.toEpochSecond(zone)) + " seconds");
    }

    @Scheduled(cron = "*/20 * * * * *")
    @TrackExecutionTime(description = "Actualize wines Job")
    public void parsePages() {
        long fetchStart = System.nanoTime();
        Document document = amClient.getPage(pageCursor.getAndIncrement());
        long fetchEnd = System.nanoTime();
        metricsCollector.timeWinePageFetchingDuration(fetchEnd - fetchStart);
        if(document != null) {
            final Long pageAmount = amService.getCatalogPagesAmount(document);
            if(pageCursor.longValue() >= pageAmount) {
                pageCursor.set(1L);
            }
            List<AmWine> wines = amService.getAmWines(document);
            updateService.updateWines(wines);
        }
    }

    @Scheduled(cron = "*/20 * * * * *")
    public void parseProps() {
        Wine wine = searchService.nWine(proprsCursor.getAndIncrement());
        if (wine == null) {
            proprsCursor.set(1);
            parseProps();
            return;
        }
        String link = wine.getLink();
        AdditionalProps props = amService.getAdditionalProps(link);
        if (props != null) {
            wine.setTaste(props.getTaste());
            wine.setFlavor(props.getFlavor());
            wine.setDescription(props.getDescription());
            wine.setRating(props.getRating());
            wine.setGastronomy(props.getGastronomy());
            wine.setActual(true);
            wine.setDateRec(new Date());
            wineRepository.save(wine);
            eventLogger.info(AmServiceNotableEvents.I_WINE_DETAILS_PARSED, link);
        }
    }
}

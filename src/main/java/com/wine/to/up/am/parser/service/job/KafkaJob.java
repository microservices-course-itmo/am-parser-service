package com.wine.to.up.am.parser.service.job;

import com.wine.to.up.am.parser.service.components.AmServiceMetricsCollector;
import com.wine.to.up.am.parser.service.domain.entity.Grape;
import com.wine.to.up.am.parser.service.domain.entity.Wine;
import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.service.SearchService;
import com.wine.to.up.am.parser.service.util.ColorConverter;
import com.wine.to.up.am.parser.service.util.ProtobufConverter;
import com.wine.to.up.am.parser.service.util.SugarConverter;
import com.wine.to.up.am.parser.service.util.log.TrackExecutionTime;
import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author : skorz
 * @since : 22.03.2021, пн
 **/

@Component
@Slf4j
public class KafkaJob {

    private static AtomicInteger cursor = new AtomicInteger(1);

    private SearchService searchService;

    private AmServiceMetricsCollector amServiceMetricsCollector;

    private KafkaMessageSender<ParserApi.WineParsedEvent> kafkaMessageSender;

    private static final String SHOP_LINK = "amwine.com";

    public KafkaJob(SearchService searchService,
                    AmServiceMetricsCollector amServiceMetricsCollector,
                    KafkaMessageSender<ParserApi.WineParsedEvent> kafkaMessageSender) {
        this.searchService = searchService;
        this.amServiceMetricsCollector = amServiceMetricsCollector;
        this.kafkaMessageSender = kafkaMessageSender;
    }

    @Scheduled(cron = "*/20 * * * * *")
    @TrackExecutionTime(description = "Kafka Job")
    public void sendWines() {
        Wine wine = searchService.nWine(cursor.getAndIncrement());
        if (wine == null) {
            cursor.set(1);
            sendWines();
            return;
        }
        WineDto wineDto = WineDto.builder()
                .name(wine.getName())
                .value(wine.getVolume())
                .sugar(SugarConverter.getSugar(wine.getSugar()))
                .grapes(wine.getGrapes().stream().map(Grape::getName).collect(Collectors.toList()))
                .country(wine.getCountry().getName())
                .color(ColorConverter.getColor(wine.getColor()))
                .alco(wine.getStrength())
                .picture(wine.getPictureUrl())
                .build();
        ParserApi.WineParsedEvent message = ParserApi.WineParsedEvent.newBuilder()
                .setShopLink(SHOP_LINK)
                .addWines(ProtobufConverter.getProtobufWine(wineDto))
                .build();
        kafkaMessageSender.sendMessage(message);
        amServiceMetricsCollector.countWinesPublishedToKafka(1);
    }
}

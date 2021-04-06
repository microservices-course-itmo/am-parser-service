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
                .picture(wine.getPictureUrl())
                .color(ColorConverter.getColor(wine.getColor()))
                .sugar(SugarConverter.getSugar(wine.getSugar()))
                .country(wine.getCountry().getName())
                .alco(wine.getStrength())
                .value(wine.getVolume())
                .price(wine.getPrice())
                .oldPrice(wine.getOldPrice())
                .link(wine.getLink())
                .region(wine.getRegion().getName())
                .producer(wine.getProducer().getName())
                .description(wine.getDescription())
                .flavor(wine.getFlavor())
                .gastronomy(wine.getGastronomy())
                .taste(wine.getTaste())
                .rating(wine.getRating())
                .brand(wine.getBrand().getName())
                .grapes(wine.getGrapes().stream().map(Grape::getName).collect(Collectors.toList()))
                .build();
        ParserApi.WineParsedEvent message = ParserApi.WineParsedEvent.newBuilder()
                .setShopLink(SHOP_LINK)
                .addWines(ProtobufConverter.getProtobufWine(wineDto))
                .build();
        kafkaMessageSender.sendMessage(message);
        amServiceMetricsCollector.countWinesPublishedToKafka(1);
    }
}

package com.wine.to.up.am.parser.service.controller;

import com.wine.to.up.am.parser.service.components.AmServiceMetricsCollector;
import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.service.SearchService;
import com.wine.to.up.am.parser.service.util.ProtobufConverter;
import com.wine.to.up.am.parser.service.util.TrackExecutionTime;
import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/parser")
@Slf4j
public class KafkaController {

    @Resource
    private KafkaMessageSender<ParserApi.WineParsedEvent> kafkaMessageSender;
    @Resource
    private SearchService searchService;
    @Resource
    private AmServiceMetricsCollector amServiceMetricsCollector;


    private static final String SHOP_LINK = "amwine.com";
    private static final int CHUNK_SIZE = 1000;

    /**
     * Отправка всех вин из БД в Кафку.
     */
    @GetMapping("/update")
    @TrackExecutionTime
    public void sendAllWines() {

        try {
            List<WineDto> wineDtoList = searchService.findAll();
            List<ParserApi.Wine> wines = new ArrayList<>();
            for (WineDto wineDto : wineDtoList) {
                wines.add(ProtobufConverter.getProtobufWine(wineDto));
            }
            amServiceMetricsCollector.countWinesPublishedToKafka(wines.size());
            List<List<ParserApi.Wine>> chunks = chunkify(wines, CHUNK_SIZE);

            for(List<ParserApi.Wine> chunk : chunks) {
                ParserApi.WineParsedEvent message = ParserApi.WineParsedEvent.newBuilder()
                        .setShopLink(SHOP_LINK)
                        .addAllWines(chunk)
                        .build();
                kafkaMessageSender.sendMessage(message);
            }
        } catch (Exception exception) {
            log.error("Can't export wines list", exception);
        }

    }

    public List<List<ParserApi.Wine>> chunkify(List<ParserApi.Wine> list, int chunkSize) {
        List<List<ParserApi.Wine>> chunks = new ArrayList<>();

        for (int i = 0; i < list.size(); i += chunkSize) {
            List<ParserApi.Wine> chunk = new ArrayList<>(list.subList(i, Math.min(list.size(), i + chunkSize)));
            chunks.add(chunk);
        }

        return chunks;
    }
}

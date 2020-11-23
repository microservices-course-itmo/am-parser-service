package com.wine.to.up.am.parser.service.controller;

import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.service.SearchService;
import com.wine.to.up.am.parser.service.util.ProtobufConverter;
import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/send")
@Slf4j
public class KafkaController {

    @Resource
    private KafkaMessageSender<ParserApi.WineParsedEvent> kafkaMessageSender;
    @Resource
    private SearchService searchService;


    private static final String SHOP_LINK = "amwine.com";

    /**
     * Отправка всех вин из БД в Кафку.
     */
    @PostMapping("/sendAllWines")
    public void sendAllWines() {
        long startTime = new Date().getTime();
        log.info("Start method at {}", startTime);

        try {
            List<WineDto> wineDtoList = searchService.findAll();
            List<ParserApi.Wine> wines = new ArrayList<>();
            for (WineDto wineDto : wineDtoList) {
                wines.add(ProtobufConverter.getProtobufWine(wineDto));
            }

            ParserApi.WineParsedEvent message = ParserApi.WineParsedEvent.newBuilder()
                    .setShopLink(SHOP_LINK)
                    .addAllWines(wines)
                    .build();

            kafkaMessageSender.sendMessage(message);
        } catch (Exception exception) {
            log.error("Can't export wines list", exception);
        }

        log.info("End method at {}; duration = {}", new Date().getTime(), (new Date().getTime() - startTime));
    }
}

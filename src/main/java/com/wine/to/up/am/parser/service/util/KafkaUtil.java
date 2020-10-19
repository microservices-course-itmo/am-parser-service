package com.wine.to.up.am.parser.service.util;

import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : SSyrova
 * @since : 19.10.2020, пн
 **/
@Component
public class KafkaUtil {

    @Value("#{${am.parser.kafka.color-map}}")
    private Map<String, Integer> colorMap;

    @Value("#{${am.parser.kafka.sugar-map}}")
    private Map<String, Integer> sugarMap;

    public ParserApi.Wine.Color toKafkaColor(String color) {
        if (colorMap.containsKey(color)) {
            Integer colorInt = colorMap.get(color);
            return ParserApi.Wine.Color.forNumber(colorInt);
        } else {
            return ParserApi.Wine.Color.UNRECOGNIZED;
        }
    }

    public ParserApi.Wine.Sugar toKafkaSugar(String sugar) {
        if (colorMap.containsKey(sugar)) {
            Integer sugarInt = sugarMap.get(sugar);
            return ParserApi.Wine.Sugar.forNumber(sugarInt);
        } else {
            return ParserApi.Wine.Sugar.UNRECOGNIZED;
        }
    }

}

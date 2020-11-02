package com.wine.to.up.am.parser.service.util;

import com.wine.to.up.am.parser.service.domain.entity.Color;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ColorConverter {

    RED("Красное", ParserApi.Wine.Color.RED),
    ROSE("Розовое", ParserApi.Wine.Color.ROSE),
    WHITE("Белое", ParserApi.Wine.Color.WHITE),
    UNRECOGNIZED("", ParserApi.Wine.Color.UNRECOGNIZED);

    private final String color;
    private final ParserApi.Wine.Color apiColor;

    ColorConverter(String color, ParserApi.Wine.Color apiColor) {
        this.color = color;
        this.apiColor = apiColor;
    }

    public static ParserApi.Wine.Color getApiColor(String color) {
        return Arrays.stream(ColorConverter.values()).filter(t -> t != null && t.getColor().equals(color))
                .findFirst()
                .orElse(RED)//2020-10-26 ksv: TODO Sprint6 - Изменить после починки UNRECOGNIZED или изменения модели.
                .getApiColor();
    }

    public static String getColor(Color color) {
        return color != null ? color.getName() : null;
    }
}

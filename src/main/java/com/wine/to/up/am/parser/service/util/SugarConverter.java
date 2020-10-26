package com.wine.to.up.am.parser.service.util;

import com.wine.to.up.am.parser.service.domain.entity.Sugar;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SugarConverter {

    DRY("Сухое", ParserApi.Wine.Sugar.DRY),
    MEDIUM("Полусладкое", ParserApi.Wine.Sugar.MEDIUM),
    MEDIUM_DRY("Полусухое", ParserApi.Wine.Sugar.MEDIUM_DRY),
    SWEET("Сладкое", ParserApi.Wine.Sugar.SWEET),
    UNRECOGNIZED("", ParserApi.Wine.Sugar.UNRECOGNIZED);


    private final String sugar;
    private final ParserApi.Wine.Sugar apiSugar;

    SugarConverter(String sugar, ParserApi.Wine.Sugar apiSugar) {
        this.sugar = sugar;
        this.apiSugar = apiSugar;
    }

    public static ParserApi.Wine.Sugar getApiSugar(String sugar) {
        return Arrays.stream(SugarConverter.values()).filter(t -> t != null && t.getSugar().equals(sugar))
                .findFirst()
                .orElse(DRY) //2020-10-26 ksv: TODO Sprint6 - Изменить после починки UNRECOGNIZED или изменения модели.
                .getApiSugar();
    }

    public static String getSugar(Sugar sugar) {
        return sugar != null ? sugar.getName() : null;
    }
}

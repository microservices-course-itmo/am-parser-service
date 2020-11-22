package com.wine.to.up.am.parser.service.util;

import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProtobufConverter {

    public static ParserApi.Wine getProtobufWine(WineDto wineDto) {
        ParserApi.Wine.Builder builder = ParserApi.Wine.newBuilder();
        List<String> regions = new ArrayList<>();
        if (StringUtils.hasText(wineDto.getRegion())) {
            regions.add(wineDto.getRegion());
        }
        builder.addAllRegion(regions);
        if (StringUtils.hasText(wineDto.getName())) {
            builder.setName(wineDto.getName());
        }
        if (StringUtils.hasText(wineDto.getPicture())) {
            builder.setImage(wineDto.getPicture());
        }
        if (StringUtils.hasText(wineDto.getCountry())) {
            builder.setCountry(wineDto.getCountry());
        }
        if (wineDto.getAlco() != null) {
            builder.setStrength(wineDto.getAlco().floatValue());
        }
        if (wineDto.getValue() != null) {
            builder.setCapacity(wineDto.getValue().floatValue());
        }
        if (wineDto.getGrapes() != null && !wineDto.getGrapes().isEmpty()) {
            builder.addAllGrapeSort(wineDto.getGrapes());
        }
        if (wineDto.getPrice() != null) {
            builder.setNewPrice(wineDto.getPrice().floatValue());
        }
        if (wineDto.getOldPrice() != null) {
            builder.setOldPrice(wineDto.getOldPrice().floatValue());
        }
        if (wineDto.getRating() != null) {
            builder.setRating(wineDto.getRating().floatValue());
        }
        if (StringUtils.hasText(wineDto.getProducer())) {
            builder.setManufacturer(wineDto.getProducer());
        }
        if (StringUtils.hasText(wineDto.getLink())) {
            builder.setLink(wineDto.getLink());
        }
        if (StringUtils.hasText(wineDto.getFlavor())) {
            builder.setFlavor(wineDto.getFlavor());
        }
        if (StringUtils.hasText(wineDto.getTaste())) {
            builder.setTaste(wineDto.getTaste());
        }
        if (StringUtils.hasText(wineDto.getDescription())) {
            builder.setDescription(wineDto.getDescription());
        }
        if (StringUtils.hasText(wineDto.getGastronomy())) {
            builder.setGastronomy(wineDto.getGastronomy());
        }
        if (StringUtils.hasText(wineDto.getBrand())) {
            builder.setBrand(wineDto.getBrand());
        }

        builder.setSugar(SugarConverter.getApiSugar(wineDto.getSugar()));
        builder.setColor(ColorConverter.getApiColor(wineDto.getColor()));
        return builder.build();
    }
}

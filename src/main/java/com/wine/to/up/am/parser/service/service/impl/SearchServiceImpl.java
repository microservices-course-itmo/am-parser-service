package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.Grape;
import com.wine.to.up.am.parser.service.domain.entity.Wine;
import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.repository.WineRepository;
import com.wine.to.up.am.parser.service.service.SearchService;
import com.wine.to.up.am.parser.service.util.ColorConverter;
import com.wine.to.up.am.parser.service.util.SugarConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Service
public class SearchServiceImpl implements SearchService {

    private final WineRepository wineRepository;

    @Value(value = "${am.site.base-url}")
    private String baseUrl;

    public SearchServiceImpl(WineRepository wineRepository) {
        this.wineRepository = wineRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WineDto> findAllLessByRub(Double price) {
        List<Wine> wines = wineRepository.findAllByPriceLessThan(price);
        return wines
                .stream()
                .map(e -> WineDto.builder()
                        .name(e.getName())
                        .value(e.getVolume())
                        .sugar(SugarConverter.getSugar(e.getSugar()))
                        .grapes(e.getGrapes().stream().map(Grape::getName).collect(Collectors.toList()))
                        .country(e.getCountry().getName())
                        .color(ColorConverter.getColor(e.getColor()))
                        .alco(e.getStrength())
                        .picture(e.getPictureUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<WineDto> findAll() {
        List<Wine> wines = wineRepository.findAll();
        return wines
                .stream()
                .map(e -> WineDto.builder()
                        .name(e.getName())
                        .value(e.getVolume())
                        .brand(e.getBrand() != null ? e.getBrand().getName() : null)
                        .sugar(SugarConverter.getSugar(e.getSugar()))
                        .grapes(e.getGrapes().stream().filter(Objects::nonNull).map(Grape::getName).collect(Collectors.toList()))
                        .country(e.getCountry() != null ? e.getCountry().getName() : null)
                        .color(ColorConverter.getColor(e.getColor()))
                        .alco(e.getStrength())
                        .price(e.getPrice())
                        .flavor(e.getFlavor())
                        .description(e.getDescription())
                        .rating(e.getRating())
                        .taste(e.getTaste())
                        .gastronomy(e.getGastronomy())
                        .oldPrice(e.getOldPrice())
                        .link(e.getLink())
                        .region(e.getRegion() != null ? e.getRegion().getName() : null)
                        .producer(e.getProducer() != null ? e.getProducer().getName() : null)
                        .picture(baseUrl + e.getPictureUrl())
                        .build())
                .collect(Collectors.toList());
    }
}

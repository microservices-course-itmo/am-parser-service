package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.Grape;
import com.wine.to.up.am.parser.service.domain.entity.Wine;
import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.repository.WineRepository;
import com.wine.to.up.am.parser.service.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private WineRepository wineRepository;

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
                        .sugar(e.getSugar().getName())
                        .grapes(e.getGrapes().stream().map(Grape::getName).collect(Collectors.toList()))
                        .country(e.getCountry().getName())
                        .color(e.getName())
                        .alco(e.getStrength())
                        .picture(e.getPictureUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<WineDto> findAll() {
        List<Wine> wines = StreamSupport
                .stream(wineRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        return wines
                .stream()
                .map(e -> WineDto.builder()
                        .name(e.getName())
                        .value(e.getVolume())
                        .sugar(e.getSugar().getName())
                        .grapes(e.getGrapes().stream().map(Grape::getName).collect(Collectors.toList()))
                        .country(e.getCountry().getName())
                        .color(e.getName())
                        .alco(e.getStrength())
                        .picture(e.getPictureUrl())
                        .build())
                .collect(Collectors.toList());
    }
}

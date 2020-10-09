package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.service.RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Service
@Slf4j
public class RestServiceStub implements RestService {

    @Override
    public List<WineDto> findAllLessByRub(Double price) {
        return Collections.nCopies(10, WineDto.builder().build());
    }

    @Override
    public void updateDictionary() {
        log.info("updated dict.");
    }

    @Override
    public void updateWines() {
        log.info("updated wines.");
    }

    @Override
    public void updateAll() {
        log.info("updated all.");
    }
}

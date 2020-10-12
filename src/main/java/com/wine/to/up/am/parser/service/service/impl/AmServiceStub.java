package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.model.dto.AmWine;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.service.AmService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Service
public class AmServiceStub implements AmService {

    @Override
    public List<WineDto> getWines() {
        return Collections.nCopies(10, WineDto.builder().build());
    }

    @Override
    public List<AmWine> getAmWines() {
        return null;
    }

    @Override
    public Dictionary getDictionary() {
        return new Dictionary();
    }
}

package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Service
public class SearchServiceStub implements SearchService {

    @Override
    public List<WineDto> findAllLessByRub(Double price) {
        return Collections.nCopies(10, WineDto.builder().build());
    }
}

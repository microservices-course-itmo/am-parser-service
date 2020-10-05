package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.model.dto.Catalog;
import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.service.AmClient;
import com.wine.to.up.am.parser.service.service.AmParserService;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : SSyrova
 * @since : 29.09.2020, вт
 **/
@Component
public class AmParserServiceStub implements AmParserService {

    @Autowired
    @Qualifier("amClientStub")
    private AmClient amClient;

    @Override
    public List<WineDto> parsePage(Document document) {
        /*
            Этот кусок кода переводит с помощью каталога информацию
         */
        return Arrays.asList(new WineDto(), new WineDto(), new WineDto());
    }

    @Override
    public Long getCatalogPagesAmount(Document document) {
        return 457L;
    }

    @Override
    public Catalog parseCatalog(Document document) {
        return new Catalog();
    }
}
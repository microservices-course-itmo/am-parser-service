package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.service.AmCatalogService;
import com.wine.to.up.am.parser.service.service.AmClient;
import com.wine.to.up.am.parser.service.service.AmParserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AmParserServiceImplTest {

    @Autowired
    AmParserService amParserService;

    @Autowired
    AmClient amClient;

    @Test
    public void parsePage() throws IOException {
        var input = new File("src/test/java/com/wine/to/up/am/parser/service/service/impl/mainPageSample.html");
        var document = Jsoup.parse(input, "UTF-8", "");
        var wineList = amParserService.parsePage(document);
        assertEquals(1, wineList.size());
    }

    @Test
    public void getCatalogPagesAmount() throws IOException {
        File input = new File("src/test/java/com/wine/to/up/am/parser/service/service/impl/mainPageSample.html");
        Document document = Jsoup.parse(input, "UTF-8", "");
        var pages = amParserService.getCatalogPagesAmount(document);
        assertEquals((Long) 458L, pages);
    }

    @Test
    public void parseCatalog() throws IOException {
        File input = new File("src/test/java/com/wine/to/up/am/parser/service/service/impl/responseSample.html");
        Document document = Jsoup.parse(input, "UTF-8", "");
        var catalog = amParserService.parseCatalog(document);
        assertEquals(1, catalog.getColors().size());
        assertEquals(1, catalog.getBrands().size());
        assertEquals(1, catalog.getCountries().size());
        assertEquals(1, catalog.getGrapes().size());
        assertEquals(1, catalog.getSugars().size());
    }
}
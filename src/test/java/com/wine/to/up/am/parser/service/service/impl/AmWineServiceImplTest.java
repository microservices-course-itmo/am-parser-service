package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.service.AmClient;
import com.wine.to.up.am.parser.service.service.AmParserService;
import com.wine.to.up.am.parser.service.service.AmWineService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
/**
 * @author : bgubanov
 * @since : 11.10.2020
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AmWineServiceImplTest {

    @MockBean
    AmClient amClient;

    @MockBean
    AmParserService amParserService;

    @InjectMocks
    AmWineServiceImpl amWineService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAllAmWines() throws IOException {
        doReturn(1L).when(amParserService).getCatalogPagesAmount(any());
        var arr = new ArrayList<WineDto>();
        arr.add(new WineDto());
        doReturn(arr).when(amParserService).parsePage(any());
        var input = new File("src/test/java/com/wine/to/up/am/parser/service/service/impl/mainPageSample.html");
        var document = Jsoup.parse(input, "UTF-8", "");
        doReturn(document).when(amClient).getPage(any());
        var wineList = amWineService.getAllAmWines();
        assertEquals(1, wineList.size());
    }

    @Test
    public void updateWines() {
    }
}
package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.model.dto.AmWine;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.service.AmClient;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;


import static com.wine.to.up.am.parser.service.service.impl.SampleObjects.getSampleAmWineList;
import static com.wine.to.up.am.parser.service.service.impl.SampleObjects.getSampleDictionary;
import static com.wine.to.up.am.parser.service.service.impl.SampleObjects.getSampleDoc;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author : bgubanov
 * @since : 13.10.2020
 **/

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
@Ignore
public class AmServiceImplTest {


    AmClient amClient = Mockito.mock(AmClient.class);
    AmServiceImpl amServiceMock = Mockito.mock(AmServiceImpl.class);

    @InjectMocks
    AmServiceImpl amService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAmWines() throws IOException {
        Document document = getSampleDoc();
        when(amClient.getPage(any())).thenReturn(document);
        when(amClient.getMainPage()).thenReturn(document);
        List<AmWine> listWines = amService.getAmWines();
        assertEquals(1, listWines.size());
    }

    @Test
    public void getDictionary() throws IOException {
        Document document = getSampleDoc();
        when(amClient.getPage(any())).thenReturn(document);
        when(amClient.getMainPage()).thenReturn(document);
        Dictionary dict = amService.getDictionary();
        assertEquals(1, dict.getColors().size());
        assertEquals(1, dict.getBrands().size());
        assertEquals(1, dict.getCountries().size());
        assertEquals(1, dict.getGrapes().size());
        assertEquals(1, dict.getSugars().size());
    }

}
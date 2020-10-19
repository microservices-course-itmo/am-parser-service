package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.service.AmClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static com.wine.to.up.am.parser.service.service.impl.SampleObjects.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author : bgubanov
 * @since : 13.10.2020
 **/

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
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
    public void getWines() throws IOException {
        when(amServiceMock.getDictionary()).thenReturn(getSampleDictionary());
        when(amServiceMock.getAmWines()).thenReturn(getSampleAmWineList());
        var document = getSampleDoc();
        when(amClient.getMainPage()).thenReturn(document);
        when(amClient.getPage(any())).thenReturn(document);
        var a = amService.getWines();
        assertEquals(1, a.size());
        var wine = a.get(0);
        assertEquals("wine", wine.getName());
        assertEquals("http", wine.getPicture());
        assertEquals("color", wine.getColor());
        assertEquals("sugar", wine.getSugar());
        assertEquals("country", wine.getCountry());
        assertEquals(1, wine.getGrapes().size());
        assertEquals("grape", wine.getGrapes().get(0));
        assertEquals((Double) 0.0, wine.getAlco());
        assertEquals((Double) 0.0, wine.getValue());
    }

    @Test
    public void getAmWines() throws IOException {
        var document = getSampleDoc();
        when(amClient.getPage(any())).thenReturn(document);
        when(amClient.getMainPage()).thenReturn(document);
        var listWines = amService.getAmWines();
        assertEquals(1, listWines.size());
    }

    @Test
    public void getDictionary() throws IOException {
        var document = getSampleDoc();
        when(amClient.getPage(any())).thenReturn(document);
        when(amClient.getMainPage()).thenReturn(document);
        var dict = amService.getDictionary();
        assertEquals(1, dict.getColors().size());
        assertEquals(1, dict.getBrands().size());
        assertEquals(1, dict.getCountries().size());
        assertEquals(1, dict.getGrapes().size());
        assertEquals(1, dict.getSugars().size());
    }

}
package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.Wine;
import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.repository.WineRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.wine.to.up.am.parser.service.service.impl.SampleObjects.getSampleWineList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author : bgubanov
 * @since : 13.10.2020
 **/
@ExtendWith(MockitoExtension.class)
public class SearchServiceImplTest {

    private final WineRepository wineRepositoryMock = Mockito.mock(WineRepository.class);

    @InjectMocks
    private SearchServiceImpl searchService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findAllLessByRub() {
        List<Wine> wines = getSampleWineList();
        when(wineRepositoryMock.findAllByPriceLessThan(5d)).thenReturn(wines);
        when(wineRepositoryMock.findAllByPriceLessThan(-5d)).thenReturn(new ArrayList<>());
        List<WineDto> listWines = searchService.findAllLessByRub(5d);
        assertEquals(1, listWines.size());
        WineDto wine = listWines.get(0);
        assertEquals("wine", wine.getName());
        assertEquals("http", wine.getPicture());
        assertEquals("color", wine.getColor());
        assertEquals("sugar", wine.getSugar());
        assertEquals("country", wine.getCountry());
        assertEquals(1, wine.getGrapes().size());
        assertEquals("grape", wine.getGrapes().get(0));
        assertEquals((Double) 0.0, wine.getAlco());
        assertEquals((Double) 0.0, wine.getValue());
        List<WineDto> listWinesEmpty = searchService.findAllLessByRub(-5d);
        assertEquals(0, listWinesEmpty.size());
    }

    @Test
    public void findAll() {
        var wines = getSampleWineList();
        when(wineRepositoryMock.findAll()).thenReturn(wines);
        var listWines = searchService.findAll();
        assertEquals(1, listWines.size());
        var wine = listWines.get(0);
        assertEquals("wine", wine.getName());
        assertEquals("nullhttp", wine.getPicture());
        assertEquals("color", wine.getColor());
        assertEquals("sugar", wine.getSugar());
        assertEquals("country", wine.getCountry());
        assertEquals(1, wine.getGrapes().size());
        assertEquals("grape", wine.getGrapes().get(0));
        assertEquals((Double) 0.0, wine.getAlco());
        assertEquals((Double) 0.0, wine.getValue());
    }
}
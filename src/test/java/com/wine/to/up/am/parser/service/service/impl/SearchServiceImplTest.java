package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static com.wine.to.up.am.parser.service.service.impl.SampleRepositoryObjects.*;
import static org.junit.Assert.*;

/**
 * @author : bgubanov
 * @since : 13.10.2020
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchServiceImplTest {

    @Autowired
    WineRepository wineRepository;

    @Autowired
    SugarRepository sugarRepository;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ColorRepository colorRepository;

    @Autowired
    GrapeRepository grapeRepository;

    @InjectMocks
    SearchServiceImpl searchService;

    @Before
    public void init() {
        wineRepository.deleteAll();
        sugarRepository.deleteAll();
        grapeRepository.deleteAll();
        countryRepository.deleteAll();
        colorRepository.deleteAll();
        brandRepository.deleteAll();
    }

    @Test
    public void findAllLessByRub() {
        sugarRepository.save(getSampleSugarEntity());
        grapeRepository.save(getSampleGrapeEntity());
        countryRepository.save(getSampleCountryEntity());
        colorRepository.save(getSampleColorEntity());
        brandRepository.save(getSampleBrandEntity());
        wineRepository.save(getSampleWineEntity());
        var list = wineRepository.findAllByPriceLessThan(3.0);
        var a = "";
    }
}
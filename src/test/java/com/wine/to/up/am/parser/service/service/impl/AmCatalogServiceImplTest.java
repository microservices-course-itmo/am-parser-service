package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.*;
import com.wine.to.up.am.parser.service.model.dto.Catalog;
import com.wine.to.up.am.parser.service.repository.*;
import com.wine.to.up.am.parser.service.service.AmCatalogService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * @author : SSyrova
 * @since : 05.10.2020, пн
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class AmCatalogServiceImplTest {

    @Autowired
    AmCatalogService amCatalogService;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ColorRepository colorRepository;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    SugarRepository sugarRepository;

    @Autowired
    GrapeRepository grapeRepository;

    @Test
    public void getCatalog() throws IOException {
        File input = new File("src/test/java/com/wine/to/up/am/parser/service/service/impl/responseSample.html");
        Document document = Jsoup.parse(input, "UTF-8", "");
        var catalog = amCatalogService.getCatalog(document);
        assertEquals(1, catalog.getColors().size());
        assertEquals(1, catalog.getBrands().size());
        assertEquals(1, catalog.getCountries().size());
        assertEquals(1, catalog.getGrapes().size());
        assertEquals(1, catalog.getSugars().size());
    }

    @Test
    public void updateCatalog() {
        var catalog = new Catalog();
        var brands = new ArrayList<Catalog.CatalogProp>();
        var colors = new ArrayList<Catalog.CatalogProp>();
        var countries = new ArrayList<Catalog.CatalogProp>();
        var grapes = new ArrayList<Catalog.CatalogProp>();
        var sugars = new ArrayList<Catalog.CatalogProp>();
        catalog.setBrands(brands);
        catalog.setColors(colors);
        catalog.setCountries(countries);
        catalog.setGrapes(grapes);
        catalog.setSugars(sugars);
        amCatalogService.updateCatalog(catalog);

        assertEquals(0, ((ArrayList<Brand>) brandRepository.findAll()).size());
        assertEquals(0, ((ArrayList<Color>) colorRepository.findAll()).size());
        assertEquals(0, ((ArrayList<Country>) countryRepository.findAll()).size());
        assertEquals(0, ((ArrayList<Grape>) grapeRepository.findAll()).size());
        assertEquals(0, ((ArrayList<Sugar>) sugarRepository.findAll()).size());

        brands.add(new Catalog.CatalogProp("1", "brand"));
        colors.add(new Catalog.CatalogProp("1", "brand"));
        countries.add(new Catalog.CatalogProp("1", "brand"));
        grapes.add(new Catalog.CatalogProp("1", "brand"));
        sugars.add(new Catalog.CatalogProp("1", "brand"));
        amCatalogService.updateCatalog(catalog);

        assertEquals(1, ((ArrayList<Brand>) brandRepository.findAll()).size());
        assertEquals(1, ((ArrayList<Color>) colorRepository.findAll()).size());
        assertEquals(1, ((ArrayList<Country>) countryRepository.findAll()).size());
        assertEquals(1, ((ArrayList<Grape>) grapeRepository.findAll()).size());
        assertEquals(1, ((ArrayList<Sugar>) sugarRepository.findAll()).size());
    }
}
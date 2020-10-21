package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.model.dto.AmWine;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author : bgubanov
 * @since : 13.10.2020
 **/

public class SampleObjects {

    public static AmWine.Props getSampleProps() {
        var props = new AmWine.Props();
        props.setAlco(0.0);
        props.setBrand("brand");
        props.setColor(0L);
        props.setCountry("country");
        var grapes = new ArrayList<String>();
        grapes.add("grape");
        props.setGrapes(grapes);
        props.setValue(0.0);
        props.setSugar(0L);
        return props;
    }

    public static List<AmWine> getSampleAmWineList() {
        var listWines = new ArrayList<AmWine>();
        listWines.add(getSampleAmWine());
        return listWines;
    }

    public static AmWine getSampleAmWine() {
        var amWine = new AmWine();

        amWine.setId("0");
        amWine.setName("wine");
        amWine.setPictureUrl("http");
        amWine.setSort(200L);
        amWine.setProps(getSampleProps());
        return amWine;
    }


    public static Document getSampleDoc() throws IOException {
        File input = new File("src/test/java/com/wine/to/up/am/parser/service/service/impl/mainPageSample.html");
        return Jsoup.parse(input, "UTF-8", "");
    }

    public static Dictionary.CatalogProp getSampleSugarCatalogProp() {
        var sampleSugar = new Dictionary.CatalogProp();
        sampleSugar.setImportId("0");
        sampleSugar.setValue("0");
        return sampleSugar;
    }

    public static Dictionary.CatalogProp getSampleBrandCatalogProp() {
        var sampleBrand = new Dictionary.CatalogProp();
        sampleBrand.setImportId("0");
        sampleBrand.setValue("brand");
        return sampleBrand;
    }

    public static Dictionary.CatalogProp getSampleCountryCatalogProp() {
        var sampleCountry = new Dictionary.CatalogProp();
        sampleCountry.setImportId("0");
        sampleCountry.setValue("country");
        return sampleCountry;
    }

    public static Dictionary.CatalogProp getSampleGrapeCatalogProp() {
        var sampleGrape = new Dictionary.CatalogProp();
        sampleGrape.setImportId("0");
        sampleGrape.setValue("grape");
        return sampleGrape;
    }

    public static Dictionary.CatalogProp getSampleColorCatalogProp() {
        var sampleColor = new Dictionary.CatalogProp();
        sampleColor.setImportId("0");
        sampleColor.setValue("color");
        return sampleColor;
    }

    public static Dictionary getSampleDictionary() {
        var dictionary = new Dictionary();

        var sugars = new HashMap<String, Dictionary.CatalogProp>();
        var sampleSugar = getSampleSugarCatalogProp();
        sugars.put("0", sampleSugar);

        var colors = new HashMap<String, Dictionary.CatalogProp>();
        var sampleColor = getSampleColorCatalogProp();
        colors.put("0", sampleColor);

        var countries = new HashMap<String, Dictionary.CatalogProp>();
        var sampleCountry = getSampleCountryCatalogProp();
        countries.put("0", sampleCountry);

        var brands = new HashMap<String, Dictionary.CatalogProp>();
        var sampleBrand = getSampleBrandCatalogProp();
        brands.put("0", sampleBrand);

        var grapes = new HashMap<String, Dictionary.CatalogProp>();
        var sampleGrape = getSampleGrapeCatalogProp();
        grapes.put("0", sampleGrape);

        dictionary.setBrands(brands);
        dictionary.setColors(colors);
        dictionary.setCountries(countries);
        dictionary.setGrapes(grapes);
        dictionary.setSugars(sugars);
        return dictionary;
    }
}

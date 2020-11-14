package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.Wine;
import com.wine.to.up.am.parser.service.model.dto.AmWine;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.wine.to.up.am.parser.service.service.impl.SampleRepositoryObjects.getSampleWineEntity;

/**
 * @author : bgubanov
 * @since : 13.10.2020
 **/

public class SampleObjects {

    public static AmWine.Props getSampleProps() {
        AmWine.Props props = new AmWine.Props();
        props.setAlco(0.0);
        props.setBrand("brand");
        props.setColor(0L);
        props.setCountry("country");
        ArrayList<String> grapes = new ArrayList<>();
        grapes.add("grape");
        props.setGrapes(grapes);
        props.setValue(0.0);
        props.setSugar(0L);
        return props;
    }

    public static List<AmWine> getSampleAmWineList() {
        ArrayList<AmWine> listWines = new ArrayList<>();
        listWines.add(getSampleAmWine());
        return listWines;
    }

    public static AmWine getSampleAmWine() {
        AmWine amWine = new AmWine();

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
        Dictionary.CatalogProp sampleSugar = new Dictionary.CatalogProp();
        sampleSugar.setImportId("0");
        sampleSugar.setValue("0");
        return sampleSugar;
    }

    public static Dictionary.CatalogProp getSampleBrandCatalogProp() {
        Dictionary.CatalogProp sampleBrand = new Dictionary.CatalogProp();
        sampleBrand.setImportId("0");
        sampleBrand.setValue("brand");
        return sampleBrand;
    }

    public static Dictionary.CatalogProp getSampleCountryCatalogProp() {
        Dictionary.CatalogProp sampleCountry = new Dictionary.CatalogProp();
        sampleCountry.setImportId("0");
        sampleCountry.setValue("country");
        return sampleCountry;
    }

    public static Dictionary.CatalogProp getSampleGrapeCatalogProp() {
        Dictionary.CatalogProp sampleGrape = new Dictionary.CatalogProp();
        sampleGrape.setImportId("0");
        sampleGrape.setValue("grape");
        return sampleGrape;
    }

    public static Dictionary.CatalogProp getSampleColorCatalogProp() {
        Dictionary.CatalogProp sampleColor = new Dictionary.CatalogProp();
        sampleColor.setImportId("0");
        sampleColor.setValue("color");
        return sampleColor;
    }

    public static Dictionary getSampleDictionary() {
        Dictionary dictionary = new Dictionary();

        HashMap<String, Dictionary.CatalogProp> sugars = new HashMap<>();
        Dictionary.CatalogProp sampleSugar = getSampleSugarCatalogProp();
        sugars.put("0", sampleSugar);

        HashMap<String, Dictionary.CatalogProp> colors = new HashMap<>();
        Dictionary.CatalogProp sampleColor = getSampleColorCatalogProp();
        colors.put("0", sampleColor);

        HashMap<String, Dictionary.CatalogProp> countries = new HashMap<>();
        Dictionary.CatalogProp sampleCountry = getSampleCountryCatalogProp();
        countries.put("0", sampleCountry);

        HashMap<String, Dictionary.CatalogProp> brands = new HashMap<>();
        Dictionary.CatalogProp sampleBrand = getSampleBrandCatalogProp();
        brands.put("0", sampleBrand);

        HashMap<String, Dictionary.CatalogProp> grapes = new HashMap<>();
        Dictionary.CatalogProp sampleGrape = getSampleGrapeCatalogProp();
        grapes.put("0", sampleGrape);

        dictionary.setBrands(brands);
        dictionary.setColors(colors);
        dictionary.setCountries(countries);
        dictionary.setGrapes(grapes);
        dictionary.setSugars(sugars);
        return dictionary;
    }

    public static List<Wine> getSampleWineList() {
        ArrayList<Wine> listWines = new ArrayList<>();
        listWines.add(getSampleWineEntity());
        return listWines;
    }
}

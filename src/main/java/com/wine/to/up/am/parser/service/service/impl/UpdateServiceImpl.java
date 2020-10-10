package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.*;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.repository.*;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Service
@Slf4j
public class UpdateServiceImpl implements UpdateService {

    @Autowired
    @Qualifier("amServiceImpl")
    private AmService amService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private GrapeRepository grapeRepository;

    @Autowired
    private SugarRepository sugarRepository;

    @Autowired
    private WineRepository wineRepository;

    @Override
    public void updateDictionary() {
        final Dictionary dictionary = amService.getDictionary();
        int created = 0;
        int updated = 0;
        int deleted = 0;
        for (Dictionary.CatalogProp prop : dictionary.getBrands().values()) {
            Brand brand = brandRepository.findByImportId(prop.getImportId());
            if (brand == null) {
                brandRepository.save(new Brand(prop.getImportId(), prop.getValue()));
                created++;
            } else {
                if (!brand.getName().equals(prop.getValue())) {
                    brand.setName(prop.getValue());
                    brandRepository.save(brand);
                    updated++;
                }
            }
        }
        log.info("updated {} brands", updated);
        log.info("created {} brands", created);
        log.info("deleted {} brands", deleted);
        created = 0;
        updated = 0;
        deleted = 0;

        for (Dictionary.CatalogProp prop : dictionary.getColors().values()) {
            Color color = colorRepository.findByImportId(prop.getImportId());
            if (color == null) {
                colorRepository.save(new Color(prop.getImportId(), prop.getValue()));
                created++;
            } else {
                if (!color.getName().equals(prop.getValue())) {
                    color.setName(prop.getValue());
                    colorRepository.save(color);
                    updated++;
                }
            }
        }
        log.info("updated {} colors", updated);
        log.info("created {} colors", created);
        log.info("deleted {} colors", deleted);
        created = 0;
        updated = 0;
        deleted = 0;

        for (Dictionary.CatalogProp prop : dictionary.getCountries().values()) {
            Country country = countryRepository.findByImportId(prop.getImportId());
            if (country == null) {
                countryRepository.save(new Country(prop.getImportId(), prop.getValue()));
                created++;
            } else {
                if (!country.getName().equals(prop.getValue())) {
                    country.setName(prop.getValue());
                    countryRepository.save(country);
                    updated++;
                }
            }
        }
        log.info("updated {} countries", updated);
        log.info("created {} countries", created);
        log.info("deleted {} countries", deleted);
        created = 0;
        updated = 0;
        deleted = 0;

        for (Dictionary.CatalogProp prop : dictionary.getGrapes().values()) {
            Grape grape = grapeRepository.findByImportId(prop.getImportId());
            if (grape == null) {
                grapeRepository.save(new Grape(prop.getImportId(), prop.getValue()));
                created++;
            } else {
                if (!grape.getName().equals(prop.getValue())) {
                    grape.setName(prop.getValue());
                    grapeRepository.save(grape);
                    updated++;
                }
            }
        }
        log.info("updated {} grapes", updated);
        log.info("created {} grapes", created);
        log.info("deleted {} grapes", deleted);
        created = 0;
        updated = 0;
        deleted = 0;

        for (Dictionary.CatalogProp prop : dictionary.getSugars().values()) {
            Sugar sugar = sugarRepository.findByImportId(prop.getImportId());
            if (sugar == null) {
                sugarRepository.save(new Sugar(prop.getImportId(), prop.getValue()));
                created++;
            } else {
                if (!sugar.getName().equals(prop.getValue())) {
                    sugar.setName(prop.getValue());
                    sugarRepository.save(sugar);
                    updated++;
                }
            }
        }
        log.info("updated {} sugars", updated);
        log.info("created {} sugars", created);
        log.info("deleted {} sugars", deleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWines() {
        var wines = amService.getAmWines();
        var created = 0;
        var updated = 0;
        for (var wine : wines) {
            var importId = wine.getId();
            var wineEntity = wineRepository.findByImportId(importId);
            var brand = (Brand) null;
            var countryImportId = wine.getProps().getCountry();
            var country = countryImportId == null ? null : countryRepository.findByImportId(countryImportId);
            var alco = wine.getProps().getAlco();
            if (alco == null) {
                alco = 0.0;
            }
            var colorImportId = wine.getProps().getColor();
            var color = colorImportId == null ? null : colorRepository.findByImportId(colorImportId.toString());
            var sugarImportId = wine.getProps().getSugar();
            var sugar = sugarImportId == null ? null : sugarRepository.findByImportId(sugarImportId.toString());
            var pictureUrl = wine.getPictureUrl();
            var grapes = new ArrayList<Grape>();
            var newGrapes = wine.getProps().getGrapes();
            if (newGrapes != null) {
                for (var grape : newGrapes) {
                    var newGrape = grape == null ? null : grapeRepository.findByImportId(grape);
                    if (newGrape != null) {
                        grapes.add(newGrape);
                    }
                }
            }
            var value = wine.getProps().getValue();
            if (value == null) {
                value = 0.0;
            }
            if (wineEntity == null) {
                wineRepository.save(new Wine(importId,
                        pictureUrl,
                        brand,
                        country,
                        value,
                        alco,
                        color,
                        sugar,
                        grapes,
                        0.0));
                created++;
            } else {
                var isUpdated = false;
                var oldCountry = wineEntity.getCountry();
                var countryOldImportId = oldCountry == null ? null : oldCountry.getImportId();
                var countryNewImportId = country == null ? null : country.getImportId();
                if (countryOldImportId == null || !countryOldImportId.equals(countryNewImportId)) {
                    if (!(countryOldImportId == null && countryNewImportId == null)) {
                        wineEntity.setCountry(country);
                        isUpdated = true;
                    }
                }
                var oldBrand = wineEntity.getBrand();
                var brandOldImportId = oldBrand == null ? null : oldBrand.getImportId();
                var brandNewImportId = brand == null ? null : brand.getImportId();
                if (brandOldImportId == null || !brandOldImportId.equals(brandNewImportId)) {
                    if (!(brandOldImportId == null && brandNewImportId == null)) {
                        wineEntity.setBrand(brand);
                        isUpdated = true;
                    }
                }
                var oldSugar = wineEntity.getSugar();
                var sugarOldImportId = oldSugar == null ? null : oldSugar.getImportId();
                var sugarNewImportId = sugar == null ? null : sugar.getImportId();
                if (sugarOldImportId == null || !sugarOldImportId.equals(sugarNewImportId)) {
                    if (!(sugarOldImportId == null && sugarNewImportId == null)) {
                        wineEntity.setSugar(sugar);
                        isUpdated = true;
                    }
                }
                var oldColor = wineEntity.getColor();
                var colorOldImportId = oldColor == null ? null : oldColor.getImportId();
                var colorNewImportId = color == null ? null : color.getImportId();
                if (colorOldImportId == null || !colorOldImportId.equals(colorNewImportId)) {
                    if (!(colorNewImportId == null && colorOldImportId == null)) {
                        wineEntity.setColor(color);
                        isUpdated = true;
                    }
                }
                var oldStrength = wineEntity.getStrength();
                if (oldStrength != (alco)) {
                    wineEntity.setStrength(alco);
                    isUpdated = true;
                }
                var oldGrapes = wineEntity.getGrapes();
                if (oldGrapes == null || oldGrapes.size() != grapes.size()) {
                    wineEntity.setGrapes(grapes);
                    isUpdated = true;
                } else {
                    for (int i = 0; i < oldGrapes.size(); i++) {
                        var oldGrape = oldGrapes.get(i);
                        var grape = grapes.get(i);
                        var grapeOldImportId = oldGrape == null ? null : oldGrape.getImportId();
                        var grapeNewImportId = grape == null ? null : grape.getImportId();
                        if (grapeOldImportId == null || !grapeOldImportId.equals(grapeNewImportId)) {
                            if (!(grapeNewImportId == null && grapeOldImportId == null)) {
                                wineEntity.setGrapes(grapes);
                                isUpdated = true;
                                break;
                            }
                        }
                    }
                }
                if (isUpdated) {
                    updated++;
                    wineRepository.save(wineEntity);
                }
            }
        }
        log.info("updated {} wines", updated);
        log.info("created {} wines", created);
    }
}

package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.Grape;
import com.wine.to.up.am.parser.service.domain.entity.Brand;
import com.wine.to.up.am.parser.service.domain.entity.Sugar;
import com.wine.to.up.am.parser.service.domain.entity.Color;
import com.wine.to.up.am.parser.service.domain.entity.Wine;
import com.wine.to.up.am.parser.service.domain.entity.Country;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.repository.WineRepository;
import com.wine.to.up.am.parser.service.repository.GrapeRepository;
import com.wine.to.up.am.parser.service.repository.CountryRepository;
import com.wine.to.up.am.parser.service.repository.ColorRepository;
import com.wine.to.up.am.parser.service.repository.BrandRepository;
import com.wine.to.up.am.parser.service.repository.SugarRepository;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Service
@Slf4j
public class UpdateServiceImpl implements UpdateService {

    @Resource
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateDictionary() {
        final Dictionary dictionary = amService.getDictionary();
        log.trace("Received {} brands", dictionary.getBrands().size());
        log.trace("Received {} colors", dictionary.getColors().size());
        log.trace("Received {} grapes", dictionary.getGrapes().size());
        log.trace("Received {} sugars", dictionary.getSugars().size());
        log.trace("Received {} countries", dictionary.getCountries().size());
        log.info("Received {} dictionary entries", dictionary.getBrands().size() + dictionary.getColors().size() +
                dictionary.getGrapes().size() + dictionary.getSugars().size() + dictionary.getCountries().size());

        int createdTotal = 0;
        int updatedTotal = 0;
        int deletedTotal = 0;

        int created = 0;
        int updated = 0;
        int deleted = 0;
        for (Dictionary.CatalogProp prop : dictionary.getBrands().values()) {
            Brand brand = brandRepository.findByImportId(prop.getImportId());
            if (brand == null) {
                brandRepository.save(new Brand(prop.getImportId(), prop.getValue(), true, new Date()));
                created++;
            } else {
                if (!brand.getName().equals(prop.getValue())) {
                    brand.setName(prop.getValue());
                    brandRepository.save(brand);
                    updated++;
                }
            }
        }
        log.trace("updated {} brands", updated);
        log.trace("created {} brands", created);
        log.trace("deleted {} brands", deleted);
        createdTotal += created;
        updatedTotal += updated;
        deletedTotal += deleted;
        created = 0;
        updated = 0;
        deleted = 0;

        for (Dictionary.CatalogProp prop : dictionary.getColors().values()) {
            Color color = colorRepository.findByImportId(prop.getImportId());
            if (color == null) {
                colorRepository.save(new Color(prop.getImportId(), prop.getValue(), true, new Date()));
                created++;
            } else {
                if (!color.getName().equals(prop.getValue())) {
                    color.setName(prop.getValue());
                    colorRepository.save(color);
                    updated++;
                }
            }
        }
        log.trace("updated {} colors", updated);
        log.trace("created {} colors", created);
        log.trace("deleted {} colors", deleted);
        createdTotal += created;
        updatedTotal += updated;
        deletedTotal += deleted;
        created = 0;
        updated = 0;
        deleted = 0;

        for (Dictionary.CatalogProp prop : dictionary.getCountries().values()) {
            Country country = countryRepository.findByImportId(prop.getImportId());
            if (country == null) {
                countryRepository.save(new Country(prop.getImportId(), prop.getValue(), true, new Date()));
                created++;
            } else {
                if (!country.getName().equals(prop.getValue())) {
                    country.setName(prop.getValue());
                    countryRepository.save(country);
                    updated++;
                }
            }
        }
        log.trace("updated {} countries", updated);
        log.trace("created {} countries", created);
        log.trace("deleted {} countries", deleted);
        createdTotal += created;
        updatedTotal += updated;
        deletedTotal += deleted;
        created = 0;
        updated = 0;
        deleted = 0;

        for (Dictionary.CatalogProp prop : dictionary.getGrapes().values()) {
            Grape grape = grapeRepository.findByImportId(prop.getImportId());
            if (grape == null) {
                grapeRepository.save(new Grape(prop.getImportId(), prop.getValue(), true, new Date()));
                created++;
            } else {
                if (!grape.getName().equals(prop.getValue())) {
                    grape.setName(prop.getValue());
                    grapeRepository.save(grape);
                    updated++;
                }
            }
        }
        log.trace("updated {} grapes", updated);
        log.trace("created {} grapes", created);
        log.trace("deleted {} grapes", deleted);
        createdTotal += created;
        updatedTotal += updated;
        deletedTotal += deleted;
        created = 0;
        updated = 0;
        deleted = 0;

        for (Dictionary.CatalogProp prop : dictionary.getSugars().values()) {
            Sugar sugar = sugarRepository.findByImportId(prop.getImportId());
            if (sugar == null) {
                sugarRepository.save(new Sugar(prop.getImportId(), prop.getValue(), true, new Date()));
                created++;
            } else {
                if (!sugar.getName().equals(prop.getValue())) {
                    sugar.setName(prop.getValue());
                    sugarRepository.save(sugar);
                    updated++;
                }
            }
        }
        log.trace("updated {} sugars", updated);
        log.trace("created {} sugars", created);
        log.trace("deleted {} sugars", deleted);
        createdTotal += created;
        updatedTotal += updated;
        deletedTotal += deleted;

        log.info("updated {} entries", updatedTotal);
        log.info("created {} entries", createdTotal);
        log.info("deleted {} entries", deletedTotal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWines() {
        var wines = amService.getAmWines();
        var received = wines.size();
        log.info("Received {} wines", received);
        var created = 0;
        var updated = 0;
        for (var wine : wines) {
            var importId = wine.getId();
            var wineEntity = wineRepository.findByImportId(importId);
            var brandImportId = wine.getProps().getBrand();
            var brand = brandRepository.findByImportId(brandImportId);
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
                        0.0,
                        true,
                        new Date()));
                created++;
            } else {
                var isUpdated = false;
                var oldCountry = wineEntity.getCountry();
                var countryOldImportId = oldCountry == null ? null : oldCountry.getImportId();
                var countryNewImportId = country == null ? null : country.getImportId();
                if (!Objects.equals(countryOldImportId, countryNewImportId)) {
                    wineEntity.setCountry(country);
                    isUpdated = true;
                }
                var oldBrand = wineEntity.getBrand();
                var brandOldImportId = oldBrand == null ? null : oldBrand.getImportId();
                var brandNewImportId = brand == null ? null : brand.getImportId();
                if (!Objects.equals(brandOldImportId, brandNewImportId)) {
                    wineEntity.setBrand(brand);
                    isUpdated = true;
                }
                var oldSugar = wineEntity.getSugar();
                var sugarOldImportId = oldSugar == null ? null : oldSugar.getImportId();
                var sugarNewImportId = sugar == null ? null : sugar.getImportId();
                if (!Objects.equals(sugarOldImportId, sugarNewImportId)) {
                    wineEntity.setSugar(sugar);
                    isUpdated = true;
                }
                var oldColor = wineEntity.getColor();
                var colorOldImportId = oldColor == null ? null : oldColor.getImportId();
                var colorNewImportId = color == null ? null : color.getImportId();
                if (!Objects.equals(colorOldImportId, colorNewImportId)) {
                    wineEntity.setColor(color);
                    isUpdated = true;
                }
                var oldStrength = wineEntity.getStrength();
                if (oldStrength != (alco)) {
                    wineEntity.setStrength(alco);
                    isUpdated = true;
                }
                var oldGrapes = wineEntity.getGrapes();
                if (oldGrapes.size() != grapes.size()) {
                    wineEntity.setGrapes(grapes);
                    isUpdated = true;
                } else {
                    for (int i = 0; i < oldGrapes.size(); i++) {
                        var oldGrape = oldGrapes.get(i);
                        var grape = grapes.get(i);
                        var grapeOldImportId = oldGrape == null ? null : oldGrape.getImportId();
                        var grapeNewImportId = grape == null ? null : grape.getImportId();
                        if (!Objects.equals(grapeOldImportId, grapeNewImportId)) {
                            wineEntity.setGrapes(grapes);
                            isUpdated = true;
                            break;
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
        log.trace("{} wines are already in the database and they have not changed", received - created - updated);
    }
}

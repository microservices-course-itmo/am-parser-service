package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.*;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.repository.*;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void updateWines() {
        var wines = amService.getAmWines();
        var created = 0;
        var updated = 0;
        for (var wine : wines) {
            var importId = wine.getId();
            var wineEntity = wineRepository.findByImportId(importId);
            var brand = brandRepository.findByImportId("413957");
            var country = countryRepository.findByImportId(wine.getProps().getCountry());
            var alco = wine.getProps().getAlco();
            var color = colorRepository.findByImportId(wine.getProps().getColor().toString());
            var sugar = sugarRepository.findByImportId(wine.getProps().getSugar().toString());
            var grapes = new ArrayList<Grape>();
            var pictureUrl = wine.getPictureUrl();
            for (var grape : wine.getProps().getGrapes()) {
                grapes.add(grapeRepository.findByImportId(grape));
            }
            var value = wine.getProps().getValue();
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
                      value));
                created++;
            } else {
                var isUpdated = false;
                if (!wineEntity.getCountry().getImportId().equals(country.getImportId())) {
                    wineEntity.setCountry(country);
                    isUpdated = true;
                }
                if (!wineEntity.getBrand().getImportId().equals(brand.getImportId())) {
                    wineEntity.setBrand(brand);
                    isUpdated = true;
                }
                if (!wineEntity.getSugar().getImportId().equals(sugar.getImportId())) {
                    wineEntity.setSugar(sugar);
                    isUpdated = true;
                }
                if (!wineEntity.getColor().getImportId().equals(color.getImportId())) {
                    wineEntity.setColor(color);
                    isUpdated = true;
                }
                if (wineEntity.getStrength() != (alco)) {
                    wineEntity.setStrength(alco);
                    isUpdated = true;
                }
                var oldGrapes = wineEntity.getGrapes();
                if (oldGrapes.size() != grapes.size()) {
                    wineEntity.setGrapes(grapes);
                    isUpdated = true;
                } else {
                    for (int i = 0; i < grapes.size(); i++) {
                        if (!oldGrapes.get(i).getImportId().equals(grapes.get(i).getImportId())) {
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
    }
}

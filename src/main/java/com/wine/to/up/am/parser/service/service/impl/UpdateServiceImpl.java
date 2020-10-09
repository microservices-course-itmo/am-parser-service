package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.Brand;
import com.wine.to.up.am.parser.service.domain.entity.Color;
import com.wine.to.up.am.parser.service.domain.entity.Country;
import com.wine.to.up.am.parser.service.domain.entity.Grape;
import com.wine.to.up.am.parser.service.domain.entity.Sugar;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.repository.BrandRepository;
import com.wine.to.up.am.parser.service.repository.ColorRepository;
import com.wine.to.up.am.parser.service.repository.CountryRepository;
import com.wine.to.up.am.parser.service.repository.GrapeRepository;
import com.wine.to.up.am.parser.service.repository.SugarRepository;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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

    }
}

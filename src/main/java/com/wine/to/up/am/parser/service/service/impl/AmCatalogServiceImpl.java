package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.Brand;
import com.wine.to.up.am.parser.service.domain.entity.Color;
import com.wine.to.up.am.parser.service.domain.entity.Country;
import com.wine.to.up.am.parser.service.domain.entity.Grape;
import com.wine.to.up.am.parser.service.domain.entity.Sugar;
import com.wine.to.up.am.parser.service.model.dto.Catalog;
import com.wine.to.up.am.parser.service.repository.BrandRepository;
import com.wine.to.up.am.parser.service.repository.ColorRepository;
import com.wine.to.up.am.parser.service.repository.CountryRepository;
import com.wine.to.up.am.parser.service.repository.GrapeRepository;
import com.wine.to.up.am.parser.service.repository.SugarRepository;
import com.wine.to.up.am.parser.service.service.AmCatalogService;
import com.wine.to.up.am.parser.service.service.AmClient;
import com.wine.to.up.am.parser.service.service.AmParserService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AmCatalogServiceImpl implements AmCatalogService {

    @Autowired
    private BrandRepository brandRepo;
    @Autowired
    private ColorRepository colorRepo;
    @Autowired
    private CountryRepository countryRepo;
    @Autowired
    private GrapeRepository grapeRepo;
    @Autowired
    private SugarRepository sugarRepo;
    @Autowired
    @Qualifier("amParserServiceImpl")
    private AmParserService amParserService;

    @Override
    public Catalog getCatalog(Document document) {
        return amParserService.parseCatalog(document);
    }

    @Override
    public void updateCatalog(Catalog catalog) {
        int created = 0;
        int updated = 0;
        int deleted = 0;
        for (Catalog.CatalogProp prop : catalog.getBrands()) {
            Brand brand = brandRepo.findByImportId(prop.getImportId());
            if (brand == null) {
                brandRepo.save(new Brand(prop.getImportId(), prop.getValue()));
                created++;
            } else {
                if (!brand.getName().equals(prop.getValue())) {
                    brand.setName(prop.getValue());
                    brandRepo.save(brand);
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

        for (Catalog.CatalogProp prop : catalog.getColors()) {
            Color color = colorRepo.findByImportId(prop.getImportId());
            if (color == null) {
                colorRepo.save(new Color(prop.getImportId(), prop.getValue()));
                created++;
            } else {
                if (!color.getName().equals(prop.getValue())) {
                    color.setName(prop.getValue());
                    colorRepo.save(color);
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

        for (Catalog.CatalogProp prop : catalog.getCountries()) {
            Country country = countryRepo.findByImportId(prop.getImportId());
            if (country == null) {
                countryRepo.save(new Country(prop.getImportId(), prop.getValue()));
                created++;
            } else {
                if (!country.getName().equals(prop.getValue())) {
                    country.setName(prop.getValue());
                    countryRepo.save(country);
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

        for (Catalog.CatalogProp prop : catalog.getGrapes()) {
            Grape grape = grapeRepo.findByImportId(prop.getImportId());
            if (grape == null) {
                grapeRepo.save(new Grape(prop.getImportId(), prop.getValue()));
                created++;
            } else {
                if (!grape.getName().equals(prop.getValue())) {
                    grape.setName(prop.getValue());
                    grapeRepo.save(grape);
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

        for (Catalog.CatalogProp prop : catalog.getSugars()) {
            Sugar sugar = sugarRepo.findByImportId(prop.getImportId());
            if (sugar == null) {
                sugarRepo.save(new Sugar(prop.getImportId(), prop.getValue()));
                created++;
            } else {
                if (!sugar.getName().equals(prop.getValue())) {
                    sugar.setName(prop.getValue());
                    sugarRepo.save(sugar);
                    updated++;
                }
            }
        }
        log.info("updated {} sugars", updated);
        log.info("created {} sugars", created);
        log.info("deleted {} sugars", deleted);
    }
}

package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.Grape;
import com.wine.to.up.am.parser.service.domain.entity.Brand;
import com.wine.to.up.am.parser.service.domain.entity.Sugar;
import com.wine.to.up.am.parser.service.domain.entity.Color;
import com.wine.to.up.am.parser.service.domain.entity.Wine;
import com.wine.to.up.am.parser.service.domain.entity.Country;
import com.wine.to.up.am.parser.service.model.dto.AmWine;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

        List<Brand> brandList = StreamSupport
                .stream(brandRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        for (Dictionary.CatalogProp prop : dictionary.getBrands().values()) {
            var deletionComplete = brandList.removeIf(brand -> brand.getImportId().equals(prop.getImportId()));
            brandRepository.save(new Brand(prop.getImportId(), prop.getValue(), true, new Date()));
            if (deletionComplete) {
                updated++;
            } else {
                created++;
            }
        }
        for (Brand brand : brandList) {
            if (brand.getActual()) {
                brand.setActual(false);
                brand.setDateRec(new Date());
                brandRepository.save(brand);
                updated++;
            } else {
                var timeDifference = new Date().getTime() - brand.getDateRec().getTime();
                if (timeDifference > 1000 * 3600 * 24 * 7) {
                    brandRepository.delete(brand);
                    deleted++;
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

        List<Color> colorList = StreamSupport
                .stream(colorRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        for (Dictionary.CatalogProp prop : dictionary.getColors().values()) {
            var deletionComplete = colorList.removeIf(color -> color.getImportId().equals(prop.getImportId()));
            colorRepository.save(new Color(prop.getImportId(), prop.getValue(), true, new Date()));
            if (deletionComplete) {
                updated++;
            } else {
                created++;
            }
        }
        for (Color color : colorList) {
            if (color.getActual()) {
                color.setActual(false);
                color.setDateRec(new Date());
                colorRepository.save(color);
                updated++;
            } else {
                var timeDifference = new Date().getTime() - color.getDateRec().getTime();
                if (timeDifference > 1000 * 3600 * 24 * 7) {
                    colorRepository.delete(color);
                    deleted++;
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

        List<Country> countryList = StreamSupport
                .stream(countryRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        for (Dictionary.CatalogProp prop : dictionary.getCountries().values()) {
            var deletionComplete = countryList.removeIf(country -> country.getImportId().equals(prop.getImportId()));
            countryRepository.save(new Country(prop.getImportId(), prop.getValue(), true, new Date()));
            if (deletionComplete) {
                updated++;
            } else {
                created++;
            }
        }
        for (Country country : countryList) {
            if (country.getActual()) {
                country.setActual(false);
                country.setDateRec(new Date());
                countryRepository.save(country);
                updated++;
            } else {
                var timeDifference = new Date().getTime() - country.getDateRec().getTime();
                if (timeDifference > 1000 * 3600 * 24 * 7) {
                    countryRepository.delete(country);
                    deleted++;
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

        List<Grape> grapeList = StreamSupport
                .stream(grapeRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        for (Dictionary.CatalogProp prop : dictionary.getGrapes().values()) {
            var deletionComplete = grapeList.removeIf(grape -> grape.getImportId().equals(prop.getImportId()));
            grapeRepository.save(new Grape(prop.getImportId(), prop.getValue(), true, new Date()));
            if (deletionComplete) {
                updated++;
            } else {
                created++;
            }
        }
        for (Grape grape : grapeList) {
            if (grape.getActual()) {
                grape.setActual(false);
                grape.setDateRec(new Date());
                grapeRepository.save(grape);
                updated++;
            } else {
                var timeDifference = new Date().getTime() - grape.getDateRec().getTime();
                if (timeDifference > 1000 * 3600 * 24 * 7) {
                    grapeRepository.delete(grape);
                    deleted++;
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

        List<Sugar> sugarList = StreamSupport
                .stream(sugarRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        for (Dictionary.CatalogProp prop : dictionary.getSugars().values()) {
            var deletionComplete = sugarList.removeIf(sugar -> sugar.getImportId().equals(prop.getImportId()));
            sugarRepository.save(new Sugar(prop.getImportId(), prop.getValue(), true, new Date()));
            if (deletionComplete) {
                updated++;
            } else {
                created++;
            }
        }

        for (Sugar sugar : sugarList) {
            if (sugar.getActual()) {
                sugar.setActual(false);
                sugar.setDateRec(new Date());
                sugarRepository.save(sugar);
                updated++;
            } else {
                var timeDifference = new Date().getTime() - sugar.getDateRec().getTime();
                if (timeDifference > 1000 * 3600 * 24 * 7) {
                    sugarRepository.delete(sugar);
                    deleted++;
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
        var deleted = 0;

        var wineList = StreamSupport
                .stream(wineRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        for (AmWine amWine : wines) {
            var importId = amWine.getId();

            Wine wineEntity = null;
            for (Wine wine : wineList) {
                if (wine.getImportId().equals(amWine.getId())) {
                    wineEntity = wine;
                    break;
                }
            }
            wineList.remove(wineEntity);

            var name = amWine.getName();
            var brandName = amWine.getProps().getBrand();
            var brand = brandName == null ? null : brandRepository.findByName(brandName);
            var countryImportId = amWine.getProps().getCountry();
            var country = countryImportId == null ? null : countryRepository.findByImportId(countryImportId);
            var alco = amWine.getProps().getAlco();
            if (alco == null) {
                alco = 0.0;
            }
            var price = amWine.getPrice();
            if (price == null) {
                price = 0.0;
            }
            var colorImportId = amWine.getProps().getColor();
            var color = colorImportId == null ? null : colorRepository.findByImportId(colorImportId.toString());
            var sugarImportId = amWine.getProps().getSugar();
            var sugar = sugarImportId == null ? null : sugarRepository.findByImportId(sugarImportId.toString());
            var pictureUrl = amWine.getPictureUrl();
            var grapes = new ArrayList<Grape>();
            var newGrapes = amWine.getProps().getGrapes();
            if (newGrapes != null) {
                for (var grape : newGrapes) {
                    var newGrape = grape == null ? null : grapeRepository.findByImportId(grape);
                    if (newGrape != null) {
                        grapes.add(newGrape);
                    }
                }
            }
            var value = amWine.getProps().getValue();
            if (value == null) {
                value = 0.0;
            }
            if (wineEntity != null) {
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
                var oldPrice = wineEntity.getPrice();
                if (oldPrice != price) {
                    wineEntity.setPrice(price);
                    isUpdated = true;
                }
                var oldName = wineEntity.getName();
                if (!Objects.equals(oldName, name)) {
                    wineEntity.setName(name);
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
                    wineRepository.save(new Wine(importId,
                            name,
                            pictureUrl,
                            brand,
                            country,
                            value,
                            alco,
                            color,
                            sugar,
                            grapes,
                            price,
                            true,
                            new Date()));
                } else {
                    wineEntity.setDateRec(new Date());
                    wineEntity.setActual(true);
                    wineRepository.save(wineEntity);
                }
            } else {
                wineRepository.save(new Wine(importId,
                        name,
                        pictureUrl,
                        brand,
                        country,
                        value,
                        alco,
                        color,
                        sugar,
                        grapes,
                        price,
                        true,
                        new Date()));
                created++;
            }

        }

        for (Wine wine : wineList) {
            if (wine.getActual()) {
                wine.setActual(false);
                wine.setDateRec(new Date());
                wineRepository.save(wine);
                updated++;
            } else {
                var timeDifference = new Date().getTime() - wine.getDateRec().getTime();
                if (timeDifference > 1000 * 3600 * 24 * 7) {
                    wineRepository.delete(wine);
                    deleted++;
                }
            }
        }

        log.info("updated {} wines", updated);
        log.info("created {} wines", created);
        log.info("deleted {} wines", deleted);
        log.trace("{} wines are already in the database and they have not changed", received - created - updated - deleted);

    }
}

package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.*;
import com.wine.to.up.am.parser.service.model.dto.AmWine;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.repository.*;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
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

    private int createdPropsTotal = 0;
    private int createdWinesTotal = 0;
    private int updatedPropsTotal = 0;
    private int updatedWinesTotal = 0;
    private int deletedPropsTotal = 0;
    private int deletedWinesTotal = 0;

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

        updatedPropsTotal = 0;
        createdPropsTotal = 0;
        deletedPropsTotal = 0;

        updateBrands(dictionary.getBrands().values());
        updateColors(dictionary.getColors().values());
        updateCountries(dictionary.getCountries().values());
        updateSugars(dictionary.getSugars().values());
        updateGrapes(dictionary.getGrapes().values());

        log.info("updated {} entries", updatedPropsTotal);
        log.info("created {} entries", createdPropsTotal);
        log.info("deleted {} entries", deletedPropsTotal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWines() {
        var wines = amService.getAmWines();
        var received = wines.size();
        log.info("Received {} wines", received);

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
            var brand = getBrand(amWine.getProps().getBrand());
            var country = getCountry(amWine.getProps().getCountry());
            var alco = amWine.getProps().getAlco();
            if (alco == null) {
                alco = 0.0;
            }
            var color = getColor(amWine.getProps().getColor());
            var sugar = getSugar(amWine.getProps().getSugar());
            var pictureUrl = amWine.getPictureUrl();
            var grapes = getGrapes(amWine.getProps().getGrapes());
            var value = getValue(amWine.getProps().getValue());
            if (wineEntity != null) {
                var isUpdated = updateWineEntity(wineEntity, country, brand, color, sugar, grapes, alco);
                if (isUpdated) {
                    updatedWinesTotal++;
                    wineRepository.save(Wine.newBuilder().bSetName(name).bSetImportId(importId).bSetPictureUrl(pictureUrl).bSetBrand(brand).
                            bSetCountry(country).bSetVolume(value).bSetStrength(alco).bSetColor(color).
                            bSetSugar(sugar).bSetGrapes(grapes).bSetPrice(0.0).bSetActual(true).bSetDateRec(new Date()).build());
                    //todo добавить нормальную цену
                } else {
                    wineEntity.setDateRec(new Date());
                    wineEntity.setActual(true);
                    wineRepository.save(wineEntity);
                }
            } else {
                wineRepository.save(Wine.newBuilder().bSetName(name).bSetImportId(importId).bSetPictureUrl(pictureUrl).bSetBrand(brand).
                        bSetCountry(country).bSetVolume(value).bSetStrength(alco).bSetColor(color).
                        bSetSugar(sugar).bSetGrapes(grapes).bSetPrice(0.0).bSetActual(true).bSetDateRec(new Date()).build());
                //todo добавить нормальную цену
                createdWinesTotal++;
            }
        }
        changeActual(wineList);

        log.info("updated {} wines", updatedWinesTotal);
        log.info("created {} wines", createdWinesTotal);
        log.info("deleted {} wines", deletedWinesTotal);
        log.trace("{} wines are already in the database and they have not changed", received - createdWinesTotal
                - updatedWinesTotal - deletedWinesTotal);

    }

    private double getValue(Double value) {
        if (value == null) {
            value = 0.0;
        }
        return value;
    }

    private Brand getBrand(String brandImportId) {
        return brandRepository.findByImportId(brandImportId);
    }

    private Country getCountry(String countryImportId) {
        return countryImportId == null ? null : countryRepository.findByImportId(countryImportId);
    }

    private Color getColor(Long colorImportId) {
        return colorImportId == null ? null : colorRepository.findByImportId(colorImportId.toString());
    }

    private Sugar getSugar(Long sugarImportId) {
        return sugarImportId == null ? null : sugarRepository.findByImportId(sugarImportId.toString());
    }

    private void updateBrands(Collection<Dictionary.CatalogProp> brands) {
        int created = 0;
        int updated = 0;
        int deleted = 0;

        List<Brand> brandList = StreamSupport
                .stream(brandRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        for (Dictionary.CatalogProp prop : brands) {
            var deletionComplete = brandList.removeIf(brand -> brand.getImportId().equals(prop.getImportId()));
            brandRepository.save(new Brand(prop.getImportId(), prop.getValue(), true, new Date()));
            if (deletionComplete) {
                updated++;
            } else {
                created++;
            }
        }
        for (Brand brand : brandList) {
            if (Boolean.TRUE.equals(brand.getActual())) {
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
        createdPropsTotal += created;
        updatedPropsTotal += updated;
        deletedPropsTotal += deleted;
    }

    private void updateColors(Collection<Dictionary.CatalogProp> colors) {
        int created = 0;
        int updated = 0;
        int deleted = 0;

        List<Color> colorList = StreamSupport
                .stream(colorRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        for (Dictionary.CatalogProp prop : colors) {
            var deletionComplete = colorList.removeIf(color -> color.getImportId().equals(prop.getImportId()));
            colorRepository.save(new Color(prop.getImportId(), prop.getValue(), true, new Date()));
            if (deletionComplete) {
                updated++;
            } else {
                created++;
            }
        }
        for (Color color : colorList) {
            if (Boolean.TRUE.equals(color.getActual())) {
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
        createdPropsTotal += created;
        updatedPropsTotal += updated;
        deletedPropsTotal += deleted;
    }

    private void updateCountries(Collection<Dictionary.CatalogProp> countries) {
        int created = 0;
        int updated = 0;
        int deleted = 0;

        List<Country> countryList = StreamSupport
                .stream(countryRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        for (Dictionary.CatalogProp prop : countries) {
            var deletionComplete = countryList.removeIf(country -> country.getImportId().equals(prop.getImportId()));
            countryRepository.save(new Country(prop.getImportId(), prop.getValue(), true, new Date()));
            if (deletionComplete) {
                updated++;
            } else {
                created++;
            }
        }
        for (Country country : countryList) {
            if (Boolean.TRUE.equals(country.getActual())) {
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
        createdPropsTotal += created;
        updatedPropsTotal += updated;
        deletedPropsTotal += deleted;
    }

    private void updateGrapes(Collection<Dictionary.CatalogProp> grapes) {
        int created = 0;
        int updated = 0;
        int deleted = 0;

        List<Grape> grapeList = StreamSupport
                .stream(grapeRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        for (Dictionary.CatalogProp prop : grapes) {
            var deletionComplete = grapeList.removeIf(grape -> grape.getImportId().equals(prop.getImportId()));
            grapeRepository.save(new Grape(prop.getImportId(), prop.getValue(), true, new Date()));
            if (deletionComplete) {
                updated++;
            } else {
                created++;
            }
        }
        for (Grape grape : grapeList) {
            if (Boolean.TRUE.equals(grape.getActual())) {
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
        createdPropsTotal += created;
        updatedPropsTotal += updated;
        deletedPropsTotal += deleted;
    }

    private void changeActual(List<Wine> wineList) {
        var updated = 0;
        var deleted = 0;
        for (Wine wine : wineList) {
            if (Boolean.TRUE.equals(wine.getActual())) {
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
        updatedWinesTotal += updated;
        deletedWinesTotal += deleted;
    }

    private void updateSugars(Collection<Dictionary.CatalogProp> sugars) {
        int created = 0;
        int updated = 0;
        int deleted = 0;

        List<Sugar> sugarList = StreamSupport
                .stream(sugarRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        for (Dictionary.CatalogProp prop : sugars) {
            var deletionComplete = sugarList.removeIf(sugar -> sugar.getImportId().equals(prop.getImportId()));
            sugarRepository.save(new Sugar(prop.getImportId(), prop.getValue(), true, new Date()));
            if (deletionComplete) {
                updated++;
            } else {
                created++;
            }
        }
        for (Sugar sugar : sugarList) {
            if (Boolean.TRUE.equals(sugar.getActual())) {
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
        createdPropsTotal += created;
        updatedPropsTotal += updated;
        deletedPropsTotal += deleted;

        log.info("updated {} entries", updatedPropsTotal);
        log.info("created {} entries", createdPropsTotal);
        log.info("deleted {} entries", deletedPropsTotal);
    }

    private boolean updateGrapes(Wine wineEntity, ArrayList<Grape> grapes) {
        var isUpdated = false;
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
        return isUpdated;
    }

    private boolean updateCountry(Wine wineEntity, Country country) {
        var isUpdated = false;
        var oldCountry = wineEntity.getCountry();
        var countryOldImportId = oldCountry == null ? null : oldCountry.getImportId();
        var countryNewImportId = country == null ? null : country.getImportId();
        if (!Objects.equals(countryOldImportId, countryNewImportId)) {
            wineEntity.setCountry(country);
            isUpdated = true;
        }
        return isUpdated;
    }

    private boolean updateWineEntity(Wine wineEntity, Country country, Brand brand, Color color, Sugar sugar,
                                     ArrayList<Grape> grapes, Double alco) {
        boolean isUpdated = false;
        if (updateCountry(wineEntity, country)) {
            isUpdated = true;
        }

        if (updateBrand(wineEntity, brand)) {
            isUpdated = true;
        }

        if (updateSugar(wineEntity, sugar)) {
            isUpdated = true;
        }

        if (updateColor(wineEntity, color)) {
            isUpdated = true;
        }

        var oldStrength = wineEntity.getStrength();
        if (oldStrength != (alco)) {
            wineEntity.setStrength(alco);
            isUpdated = true;
        }
        if (updateGrapes(wineEntity, grapes)) {
            isUpdated = true;
        }

        return isUpdated;
    }

    private boolean updateColor(Wine wineEntity, Color color) {
        var isUpdated = false;
        var oldColor = wineEntity.getColor();
        var colorOldImportId = oldColor == null ? null : oldColor.getImportId();
        var colorNewImportId = color == null ? null : color.getImportId();
        if (!Objects.equals(colorOldImportId, colorNewImportId)) {
            wineEntity.setColor(color);
            isUpdated = true;
        }
        return isUpdated;
    }

    private boolean updateSugar(Wine wineEntity, Sugar sugar) {
        var isUpdated = false;
        var oldSugar = wineEntity.getSugar();
        var sugarOldImportId = oldSugar == null ? null : oldSugar.getImportId();
        var sugarNewImportId = sugar == null ? null : sugar.getImportId();
        if (!Objects.equals(sugarOldImportId, sugarNewImportId)) {
            wineEntity.setSugar(sugar);
            isUpdated = true;
        }
        return isUpdated;
    }

    private boolean updateBrand(Wine wineEntity, Brand brand) {
        var isUpdated = false;
        var oldBrand = wineEntity.getBrand();
        var brandOldImportId = oldBrand == null ? null : oldBrand.getImportId();
        var brandNewImportId = brand == null ? null : brand.getImportId();
        if (!Objects.equals(brandOldImportId, brandNewImportId)) {
            wineEntity.setBrand(brand);
            isUpdated = true;
        }
        return isUpdated;
    }

    private ArrayList<Grape> getGrapes(List<String> newGrapes) {
        var grapes = new ArrayList<Grape>();
        if (newGrapes != null) {
            for (var grape : newGrapes) {
                var newGrape = grape == null ? null : grapeRepository.findByImportId(grape);
                if (newGrape != null) {
                    grapes.add(newGrape);
                }
            }
        }
        return grapes;
    }

}

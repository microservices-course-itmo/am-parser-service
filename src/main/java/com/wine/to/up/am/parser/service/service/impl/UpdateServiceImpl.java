package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.Country;
import com.wine.to.up.am.parser.service.domain.entity.Brand;
import com.wine.to.up.am.parser.service.domain.entity.Wine;
import com.wine.to.up.am.parser.service.domain.entity.Color;
import com.wine.to.up.am.parser.service.domain.entity.Grape;
import com.wine.to.up.am.parser.service.domain.entity.Sugar;
import com.wine.to.up.am.parser.service.model.dto.AmWine;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.repository.BrandRepository;
import com.wine.to.up.am.parser.service.repository.WineRepository;
import com.wine.to.up.am.parser.service.repository.CountryRepository;
import com.wine.to.up.am.parser.service.repository.GrapeRepository;
import com.wine.to.up.am.parser.service.repository.SugarRepository;
import com.wine.to.up.am.parser.service.repository.ColorRepository;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
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
        List<AmWine> wines = amService.getAmWines();
        int received = wines.size();
        log.info("Received {} wines", received);

        List<Wine> wineList = StreamSupport
                .stream(wineRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        for (AmWine amWine : wines) {
            String importId = amWine.getId();
            Wine wineEntity = null;
            for (Wine wine : wineList) {
                if (wine.getImportId().equals(amWine.getId())) {
                    wineEntity = wine;
                    break;
                }
            }
            wineList.remove(wineEntity);

            String name = amWine.getName();
            Brand brand = getBrand(amWine.getProps().getBrand());
            Country country = getCountry(amWine.getProps().getCountry());
            double strength = getStrength(amWine.getProps().getAlco());
            Color color = getColor(amWine.getProps().getColor());
            Sugar sugar = getSugar(amWine.getProps().getSugar());
            String pictureUrl = amWine.getPictureUrl();
            double price = getPrice(amWine.getPrice());
            ArrayList<Grape> grapes = getGrapes(amWine.getProps().getGrapes());
            double volume = getVolume(amWine.getProps().getValue());
            if (wineEntity != null) {
                Wine newWine = Wine.builder().name(name).importId(importId).pictureUrl(pictureUrl).brand(brand).
                        country(country).volume(volume).strength(strength).color(color).sugar(sugar).grapes(grapes).
                        price(price).actual(true).dateRec(new Date()).build();
                boolean isUpdated = updateWineEntity(wineEntity, newWine);
                if (isUpdated) {
                    updatedWinesTotal++;
                    wineRepository.save(Wine.builder().name(name).importId(importId).pictureUrl(pictureUrl).brand(brand).
                            country(country).volume(volume).strength(strength).color(color).sugar(sugar).grapes(grapes).
                            price(price).actual(true).dateRec(new Date()).build());
                } else {
                    wineEntity.setDateRec(new Date());
                    wineEntity.setActual(true);
                    wineRepository.save(wineEntity);
                }
            } else {
                wineRepository.save(Wine.builder().name(name).importId(importId).pictureUrl(pictureUrl).brand(brand).
                        country(country).volume(volume).strength(strength).color(color).sugar(sugar).grapes(grapes).
                        price(price).actual(true).dateRec(new Date()).build());
                createdWinesTotal++;
            }
        }
        changeActual(wineList);

        log.info("updated {} wines", updatedWinesTotal);
        log.info("created {} wines", createdWinesTotal);
        log.info("deleted {} wines", deletedWinesTotal);
        log.trace("{} wines are already in the database and they have not changed", received - createdWinesTotal - updatedWinesTotal - deletedWinesTotal);
    }

    private double getStrength(Double strength) {
        return strength == null ? 0.0 : strength;
    }

    private double getPrice(Double price) {
        return price == null ? 0.0 : price;
    }

    private double getVolume(Double volume) {
        return volume == null ? 0.0 : volume;
    }

    private Brand getBrand(String brandName) {
        return brandName == null ? null : brandRepository.findByName(brandName);
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
            boolean deletionComplete = brandList.removeIf(brand -> brand.getImportId().equals(prop.getImportId()));
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
                long timeDifference = new Date().getTime() - brand.getDateRec().getTime();
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
            boolean deletionComplete = colorList.removeIf(color -> color.getImportId().equals(prop.getImportId()));
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
                long timeDifference = new Date().getTime() - color.getDateRec().getTime();
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
            boolean deletionComplete = countryList.removeIf(country -> country.getImportId().equals(prop.getImportId()));
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
                long timeDifference = new Date().getTime() - country.getDateRec().getTime();
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
            boolean deletionComplete = grapeList.removeIf(grape -> grape.getImportId().equals(prop.getImportId()));
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
                long timeDifference = new Date().getTime() - grape.getDateRec().getTime();
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

    /**
     * Смена статуса вина или его удаление из БД(если оно неактуально больше недели).
     * @param wineList Список вин.
     */
    private void changeActual(List<Wine> wineList) {
        int updated = 0;
        int deleted = 0;
        for (Wine wine : wineList) {
            if (Boolean.TRUE.equals(wine.getActual())) {
                wine.setActual(false);
                wine.setDateRec(new Date());
                wineRepository.save(wine);
                updated++;
            } else {
                long timeDifference = new Date().getTime() - wine.getDateRec().getTime();
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
            boolean deletionComplete = sugarList.removeIf(sugar -> sugar.getImportId().equals(prop.getImportId()));
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
                long timeDifference = new Date().getTime() - sugar.getDateRec().getTime();
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
        boolean isUpdated = false;
        List<Grape> oldGrapes = wineEntity.getGrapes();
        if (oldGrapes.size() != grapes.size()) {
            wineEntity.setGrapes(grapes);
            isUpdated = true;
        } else {
            for (int i = 0; i < oldGrapes.size(); i++) {
                Grape oldGrape = oldGrapes.get(i);
                Grape grape = grapes.get(i);
                String grapeOldImportId = oldGrape == null ? null : oldGrape.getImportId();
                String grapeNewImportId = grape == null ? null : grape.getImportId();
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
        boolean isUpdated = false;
        Country oldCountry = wineEntity.getCountry();
        String countryOldImportId = oldCountry == null ? null : oldCountry.getImportId();
        String countryNewImportId = country == null ? null : country.getImportId();
        if (!Objects.equals(countryOldImportId, countryNewImportId)) {
            wineEntity.setCountry(country);
            isUpdated = true;
        }
        return isUpdated;
    }

    private boolean updateWineEntity(Wine wineEntity, Wine newWine) {
        boolean isUpdated = false;
        if (updateCountry(wineEntity, newWine.getCountry())) {
            isUpdated = true;
        }
        if (updateBrand(wineEntity, newWine.getBrand())) {
            isUpdated = true;
        }
        if (updateSugar(wineEntity, newWine.getSugar())) {
            isUpdated = true;
        }
        if (updateColor(wineEntity, newWine.getColor())) {
            isUpdated = true;
        }
        if (updatePrice(wineEntity, newWine.getPrice())) {
            isUpdated = true;
        }
        if (updateName(wineEntity, newWine.getName())) {
            isUpdated = true;
        }
        if (updateStrength(wineEntity, newWine.getStrength())) {
            isUpdated = true;
        }
        if (updateVolume(wineEntity, newWine.getVolume())) {
            isUpdated = true;
        }
        if (updatePicture(wineEntity, newWine.getPictureUrl())) {
            isUpdated = true;
        }

        List<Grape> grapes = newWine.getGrapes();
        if (updateGrapes(wineEntity, (ArrayList<Grape>) grapes)) {
            isUpdated = true;
        }

        return isUpdated;
    }

    private boolean updatePicture(Wine wineEntity, String pictureUrl) {
        boolean isUpdated = false;
        String oldPicture = wineEntity.getPictureUrl();
        if (!Objects.equals(oldPicture, pictureUrl)) {
            wineEntity.setPictureUrl(pictureUrl);
            isUpdated = true;
        }
        return isUpdated;
    }

    private boolean updateVolume(Wine wineEntity, double volume) {
        boolean isUpdated = false;
        double oldVolume = wineEntity.getVolume();
        if (oldVolume != (volume)) {
            wineEntity.setVolume(volume);
            isUpdated = true;
        }
        return isUpdated;
    }

    private boolean updateStrength(Wine wineEntity, double strength) {
        boolean isUpdated = false;
        double oldStrength = wineEntity.getStrength();
        if (oldStrength != (strength)) {
            wineEntity.setStrength(strength);
            isUpdated = true;
        }
        return isUpdated;
    }

    private boolean updateName(Wine wineEntity, String name) {
        boolean isUpdated = false;
        String oldName = wineEntity.getName();
        if (!Objects.equals(oldName, name)) {
            wineEntity.setName(name);
            isUpdated = true;
        }
        return isUpdated;
    }

    private boolean updatePrice(Wine wineEntity, double price) {
        boolean isUpdated = false;
        double oldPrice = wineEntity.getPrice();
        if (oldPrice != price) {
            wineEntity.setPrice(price);
            isUpdated = true;
        }
        return isUpdated;
    }

    private boolean updateColor(Wine wineEntity, Color color) {
        boolean isUpdated = false;
        Color oldColor = wineEntity.getColor();
        String colorOldImportId = oldColor == null ? null : oldColor.getImportId();
        String colorNewImportId = color == null ? null : color.getImportId();
        if (!Objects.equals(colorOldImportId, colorNewImportId)) {
            wineEntity.setColor(color);
            isUpdated = true;
        }
        return isUpdated;
    }

    private boolean updateSugar(Wine wineEntity, Sugar sugar) {
        boolean isUpdated = false;
        Sugar oldSugar = wineEntity.getSugar();
        String sugarOldImportId = oldSugar == null ? null : oldSugar.getImportId();
        String sugarNewImportId = sugar == null ? null : sugar.getImportId();
        if (!Objects.equals(sugarOldImportId, sugarNewImportId)) {
            wineEntity.setSugar(sugar);
            isUpdated = true;
        }
        return isUpdated;
    }

    private boolean updateBrand(Wine wineEntity, Brand brand) {
        boolean isUpdated = false;
        Brand oldBrand = wineEntity.getBrand();
        String brandOldImportId = oldBrand == null ? null : oldBrand.getImportId();
        String brandNewImportId = brand == null ? null : brand.getImportId();
        if (!Objects.equals(brandOldImportId, brandNewImportId)) {
            wineEntity.setBrand(brand);
            isUpdated = true;
        }
        return isUpdated;
    }

    /**
     * Получение сортов винограда по их importId в справочнике.
     * @param newGrapes Список importId сортов винограда.
     * @return Список сортов винограда.
     */
    private ArrayList<Grape> getGrapes(List<String> newGrapes) {
        ArrayList<Grape> grapes = new ArrayList<>();
        if (newGrapes != null) {
            for (String grape : newGrapes) {
                Grape newGrape = grape == null ? null : grapeRepository.findByImportId(grape);
                if (newGrape != null) {
                    grapes.add(newGrape);
                }
            }
        }
        return grapes;
    }
}

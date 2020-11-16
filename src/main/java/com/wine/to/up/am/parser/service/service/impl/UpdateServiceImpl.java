package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.Brand;
import com.wine.to.up.am.parser.service.domain.entity.Color;
import com.wine.to.up.am.parser.service.domain.entity.Country;
import com.wine.to.up.am.parser.service.domain.entity.DictionaryValue;
import com.wine.to.up.am.parser.service.domain.entity.Grape;
import com.wine.to.up.am.parser.service.domain.entity.Sugar;
import com.wine.to.up.am.parser.service.domain.entity.Wine;
import com.wine.to.up.am.parser.service.model.dto.AmWine;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.repository.BrandRepository;
import com.wine.to.up.am.parser.service.repository.ColorRepository;
import com.wine.to.up.am.parser.service.repository.CountryRepository;
import com.wine.to.up.am.parser.service.repository.GrapeRepository;
import com.wine.to.up.am.parser.service.repository.SugarRepository;
import com.wine.to.up.am.parser.service.repository.WineRepository;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

        updateInRepository(brandRepository, dictionary.getBrands(), Brand.class);
        updateInRepository(colorRepository, dictionary.getColors(), Color.class);
        updateInRepository(sugarRepository, dictionary.getSugars(), Sugar.class);
        updateInRepository(grapeRepository, dictionary.getGrapes(), Grape.class);
        updateInRepository(countryRepository, dictionary.getCountries(), Country.class);
    }

    private <T extends DictionaryValue, ID> void updateInRepository(CrudRepository<T, ID> repository,
                                                                    Map<String, Dictionary.CatalogProp> map,
                                                                    Class<T> aClass) {
        int created = 0;
        int updated = 0;
        int markForDelete = 0;
        List<T> entityList = StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
        for (T entity : entityList) {
            if (map.containsKey(entity.getImportId())) {
                Dictionary.CatalogProp prop = map.get(entity.getImportId());
                entity.setName(prop.getValue());
                entity.setDateRec(new Date());
                entity.setActual(true);
                repository.save(entity);
                map.remove(entity.getImportId());
                updated++;
            } else {
                entity.setActual(false);
                markForDelete++;
            }
        }
        for (Dictionary.CatalogProp prop : map.values()) {
            try {
                repository.save(aClass.getConstructor(String.class, String.class, Boolean.class, Date.class)
                        .newInstance(prop.getImportId(), prop.getValue(), true, new Date()));
                created++;
            } catch (Exception e) {
                log.error("Couldn't instantiate {} with error: {}", aClass.getSimpleName(), e.getMessage());
            }
        }
        log.info("created {} {}s", created, aClass.getSimpleName());
        log.info("updated {} {}s", updated, aClass.getSimpleName());
        log.info("mark for deleted {} {}s", markForDelete, aClass.getSimpleName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWines() {
        List<AmWine> wines = amService.getAmWines();
        int created = 0;
        int updated = 0;
        int markForDeleted = 0;
        Date now = new Date();
        for (AmWine amWine : wines) {
            Wine wine = wineRepository.findByImportId(amWine.getId());
            if (wine != null) {
                wine.setName(amWine.getName());
                wine.setPictureUrl(amWine.getPictureUrl());
                wine.setBrand(brandRepository.findByImportId(amWine.getProps().getBrand()));
                wine.setCountry(countryRepository.findByImportId(amWine.getProps().getCountry()));
                wine.setVolume(amWine.getProps().getValue());
                wine.setStrength(amWine.getProps().getAlco());
                wine.setColor(colorRepository.findByImportId(amWine.getProps().getColor().toString()));
                wine.setSugar(sugarRepository.findByImportId(amWine.getProps().getSugar().toString()));
                List<String> grapes = amWine.getProps().getGrapes();
                if (grapes != null && grapes.size() > 0) {
                    wine.setGrapes(grapeRepository.findAllByImportIdIn(grapes));
                }
                wine.setPrice(amWine.getPrice());

                wine.setActual(true);
                wine.setDateRec(now);
                wineRepository.save(wine);
                updated ++;
            } else {
                wine = Wine.builder()
                        .importId(amWine.getId())
                        .name(amWine.getName())
                        .pictureUrl(amWine.getPictureUrl())
                        .brand(brandRepository.findByImportId(amWine.getProps().getBrand()))
                        .country(countryRepository.findByImportId(amWine.getProps().getCountry()))
                        .volume(amWine.getProps().getValue())
                        .strength(amWine.getProps().getAlco())
                        .color(colorRepository.findByImportId(amWine.getProps().getColor().toString()))
                        .sugar(sugarRepository.findByImportId(amWine.getProps().getSugar().toString()))
                        .price(amWine.getPrice())
                        .actual(true)
                        .dateRec(now)
                        .build();
                List<String> grapes = amWine.getProps().getGrapes();
                if (grapes != null && grapes.size() > 0) {
                    wine.setGrapes(grapeRepository.findAllByImportIdIn(grapes));
                }
                wineRepository.save(wine);
                created ++;
            }
        }
        List<Wine> wineForDeleted = wineRepository.findAllByDateRecIsNot(now);
        for (Wine w : wineForDeleted) {
            w.setActual(false);
            wineRepository.save(w);
            markForDeleted ++;
        }
        log.info("created {} Wines", created);
        log.info("updated {} Wines", updated);
        log.info("mark for deleted {} Wines", markForDeleted);
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

    /**
     * Смена статуса вина или его удаление из БД(если оно неактуально больше недели).
     *
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
//        updatedWinesTotal += updated;
//        deletedWinesTotal += deleted;
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
     *
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

    @Override
    public void cleanDatabase() {
        wineRepository.deleteAll();
        brandRepository.deleteAll();
        colorRepository.deleteAll();
        countryRepository.deleteAll();
        grapeRepository.deleteAll();
        sugarRepository.deleteAll();
        wineRepository.deleteAll();
    }
}

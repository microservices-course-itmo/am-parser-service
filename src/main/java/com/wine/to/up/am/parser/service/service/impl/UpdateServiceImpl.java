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
        final List<T> entityList = StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
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
        final String name = aClass.getSimpleName();
        log.info("created {} {}s", created, name);
        log.info("updated {} {}s", updated, name);
        log.info("mark for deleted {} {}s", markForDelete, name);
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
                saveWine(wine, amWine, now);
                updated ++;
            } else {
                wine = new Wine();
                saveWine(wine, amWine, now);
                created ++;
            }
        }
        List<Wine> wineForDeleted = wineRepository.findAllByDateRecIsNot(now);
        for (Wine w : wineForDeleted) {
            w.setActual(false);
            wineRepository.save(w);
            markForDeleted++;
        }
        log.info("created {} Wines", created);
        log.info("updated {} Wines", updated);
        log.info("mark for deleted {} Wines", markForDeleted);
    }

    private void saveWine(Wine wine, AmWine amWine, Date date) {
        wine.setImportId(amWine.getId());
        wine.setName(amWine.getName());
        wine.setPictureUrl(amWine.getPictureUrl());
        wine.setPrice(amWine.getPrice());

        fillPropsValue(wine, amWine);

        wine.setActual(true);
        wine.setDateRec(date);

        wineRepository.save(wine);
    }

    private void fillPropsValue(Wine wine, AmWine amWine) {
        if (amWine.getProps() != null) {
            final AmWine.Props props = amWine.getProps();
            if (props.getBrand() != null) {
                wine.setBrand(brandRepository.findByName(amWine.getProps().getBrand()));
            }
            if (props.getCountry() != null) {
                wine.setCountry(countryRepository.findByImportId(amWine.getProps().getCountry()));
            }
            wine.setVolume(props.getValue());
            wine.setStrength(props.getAlco());
            if (props.getColor() != null) {
                wine.setColor(colorRepository.findByImportId(props.getColor().toString()));
            }
            if (props.getSugar() != null) {
                wine.setSugar(sugarRepository.findByImportId(props.getSugar().toString()));
            }
            List<String> grapes = props.getGrapes();
            if (grapes != null && grapes.size() > 0) {
                wine.setGrapes(grapeRepository.findAllByImportIdIn(grapes));
            }
        }
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

package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.components.AmServiceMetricsCollector;
import com.wine.to.up.am.parser.service.domain.entity.*;
import com.wine.to.up.am.parser.service.logging.AmServiceNotableEvents;
import com.wine.to.up.am.parser.service.model.dto.AdditionalProps;
import com.wine.to.up.am.parser.service.model.dto.AmWine;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.repository.*;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.UpdateService;
import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Service
@Slf4j
public class UpdateServiceImpl implements UpdateService {

    @Value(value = "${am.site.base-url}")
    private String baseUrl;

    private final AmService amService;

    @InjectEventLogger
    private EventLogger eventLogger;

    private final AmServiceMetricsCollector metricsCollector;

    private final BrandRepository brandRepository;

    private final ColorRepository colorRepository;

    private final CountryRepository countryRepository;

    private final GrapeRepository grapeRepository;

    private final SugarRepository sugarRepository;

    private final WineRepository wineRepository;

    private final RegionRepository regionRepository;

    private final ProducerRepository producerRepository;

    public UpdateServiceImpl(AmService amService,
                             AmServiceMetricsCollector metricsCollector,
                             BrandRepository brandRepository,
                             ColorRepository colorRepository,
                             CountryRepository countryRepository,
                             GrapeRepository grapeRepository,
                             SugarRepository sugarRepository,
                             WineRepository wineRepository,
                             RegionRepository regionRepository,
                             ProducerRepository producerRepository) {
        this.amService = amService;
        this.metricsCollector = metricsCollector;
        this.brandRepository = brandRepository;
        this.colorRepository = colorRepository;
        this.countryRepository = countryRepository;
        this.grapeRepository = grapeRepository;
        this.sugarRepository = sugarRepository;
        this.wineRepository = wineRepository;
        this.regionRepository = regionRepository;
        this.producerRepository = producerRepository;
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
        producerRepository.deleteAll();
        regionRepository.deleteAll();
    }

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
        updateInRepository(regionRepository, dictionary.getRegions(), Region.class);
        updateInRepository(producerRepository, dictionary.getProducers(), Producer.class);
    }

    private <T extends DictionaryValue, S> void updateInRepository(CrudRepository<T, S> repository,
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
        String name = aClass.getSimpleName();
        name = name.charAt(name.length() - 1) == 'y' ? name.substring(0, name.length() - 1) + "ies" : name + "s";
        log.info("created {} {}", created, name);
        log.info("updated {} {}", updated, name);
        log.info("mark for deleted {} {}", markForDelete, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWines() {
        updateWines(amService.getAmWines());
    }

    public void updateWines(List<AmWine> wines) {
        int created = 0;
        int updated = 0;
        int markForDeleted = 0;
        Date now = new Date();
        for (AmWine amWine : wines) {
            Wine wine = wineRepository.findByImportId(amWine.getId());
            if (wine != null) {
                saveWine(wine, amWine, now);
                updated++;
                metricsCollector.countNumberOfWinesUpdated();
            } else {
                wine = new Wine();
                saveWine(wine, amWine, now);
                created++;
                metricsCollector.countNumberOfWinesCreated();
            }
        }
        List<Wine> wineForDeleted = wineRepository.findAllByDateRecIsNot(now);
        for (Wine w : wineForDeleted) {
            w.setActual(false);
            wineRepository.save(w);
            markForDeleted++;
            metricsCollector.countNumberOfWinesDeleted();
        }
        log.info("created {} Wines", created);
        log.info("updated {} Wines", updated);
        log.info("mark for deleted {} Wines", markForDeleted);
    }

    @Override
    public void updateAdditionalProps() {
        List<Wine> wines = wineRepository.findAll();
        log.info("Number of wines found: {}", wines.size());
        int updated = 0;
        for (Wine wine : wines) {
            String link = wine.getLink();
            AdditionalProps props = amService.getAdditionalProps(link);
            if (props != null) {
                wine.setTaste(props.getTaste());
                wine.setFlavor(props.getFlavor());
                wine.setDescription(props.getDescription());
                wine.setRating(props.getRating());
                wine.setGastronomy(props.getGastronomy());
                wine.setActual(true);
                wine.setDateRec(new Date());
                wineRepository.save(wine);
                eventLogger.info(AmServiceNotableEvents.I_WINE_DETAILS_PARSED, link);
                updated++;
            }
        }
        log.info("Number of wines with updated additional props: {}", updated);
    }

    private void saveWine(Wine wine, AmWine amWine, Date date) {
        wine.setImportId(amWine.getId());
        wine.setName(amWine.getName());
        wine.setPictureUrl(amWine.getPictureUrl());
        wine.setPrice(amWine.getPrice());
        wine.setLink(baseUrl + amWine.getLink());

        fillPropsValue(wine, amWine);

        wine.setActual(true);
        wine.setDateRec(date);

        wineRepository.save(wine);
    }


    private void fillPropsValue(Wine wine, AmWine amWine) {
        if (amWine.getProps() != null) {
            final AmWine.Props props = amWine.getProps();
            if (props.getBrand() != null) {
                wine.setBrand(brandRepository.findFirstByName(amWine.getProps().getBrand()));
            }
            if (props.getCountry() != null) {
                wine.setCountry(countryRepository.findByImportId(amWine.getProps().getCountry()));
            }
            wine.setVolume(props.getValue());
            wine.setStrength(props.getAlco());
            wine.setOldPrice(props.getOldPrice());
            if (props.getRegion() != null) {
                wine.setRegion(regionRepository.findByImportId(props.getRegion().toString()));
            }
            if (props.getProducer() != null) {
                wine.setProducer(producerRepository.findByImportId(props.getProducer().toString()));
            }
            if (props.getColor() != null) {
                wine.setColor(colorRepository.findByImportId(props.getColor().toString()));
            }
            if (props.getSugar() != null) {
                wine.setSugar(sugarRepository.findByImportId(props.getSugar().toString()));
            }
            List<String> grapes = props.getGrapes();
            if (grapes != null && !grapes.isEmpty()) {
                wine.setGrapes(grapeRepository.findAllByImportIdIn(grapes));
            }
        }
    }
}

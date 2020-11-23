package com.wine.to.up.am.parser.service.configuration;

import com.wine.to.up.am.parser.service.components.AmServiceMetricsCollector;
import com.wine.to.up.am.parser.service.repository.BrandRepository;
import com.wine.to.up.am.parser.service.repository.ColorRepository;
import com.wine.to.up.am.parser.service.repository.CountryRepository;
import com.wine.to.up.am.parser.service.repository.GrapeRepository;
import com.wine.to.up.am.parser.service.repository.SugarRepository;
import com.wine.to.up.am.parser.service.repository.WineRepository;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.RestService;
import com.wine.to.up.am.parser.service.service.SearchService;
import com.wine.to.up.am.parser.service.service.UpdateService;
import com.wine.to.up.am.parser.service.service.impl.RestServiceImpl;
import com.wine.to.up.am.parser.service.service.impl.SearchServiceImpl;
import com.wine.to.up.am.parser.service.service.impl.UpdateServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfiguration {

    @Bean
    public SearchService searchService(WineRepository wineRepository) {
        return new SearchServiceImpl(wineRepository);
    }

    @Bean
    public UpdateService updateService(AmService amService,
                                       AmServiceMetricsCollector metricsCollector,
                                       BrandRepository brandRepository,
                                       ColorRepository colorRepository,
                                       CountryRepository countryRepository,
                                       GrapeRepository grapeRepository,
                                       SugarRepository sugarRepository,
                                       WineRepository wineRepository) {
        return new UpdateServiceImpl(amService,
                metricsCollector,
                brandRepository,
                colorRepository,
                countryRepository,
                grapeRepository,
                sugarRepository,
                wineRepository);
    }

    @Bean
    public RestService restService(SearchService searchService, UpdateService updateService) {
        return new RestServiceImpl(searchService, updateService);
    }

}

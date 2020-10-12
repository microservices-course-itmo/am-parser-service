package com.wine.to.up.am.parser.service.configuration;

import com.wine.to.up.am.parser.service.service.SearchService;
import com.wine.to.up.am.parser.service.service.UpdateService;
import com.wine.to.up.am.parser.service.service.impl.SearchServiceImpl;
import com.wine.to.up.am.parser.service.service.impl.UpdateServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestSearchBeansConfig {

    @Bean(name="searchServiceBean")
    public SearchService searchService() {
        return new SearchServiceImpl();
    }

    @Bean(name="updateServiceBean")
    public UpdateService updateService() {
        return new UpdateServiceImpl();
    }

}

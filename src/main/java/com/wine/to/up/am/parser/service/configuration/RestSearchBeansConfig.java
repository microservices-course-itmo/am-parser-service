package com.wine.to.up.am.parser.service.configuration;

import com.wine.to.up.am.parser.service.service.RestService;
import com.wine.to.up.am.parser.service.service.SearchService;
import com.wine.to.up.am.parser.service.service.UpdateService;
import com.wine.to.up.am.parser.service.service.impl.RestServiceImpl;
import com.wine.to.up.am.parser.service.service.impl.SearchServiceImpl;
import com.wine.to.up.am.parser.service.service.impl.UpdateServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestSearchBeansConfig {

    @Bean
    public SearchService searchService() {
        return new SearchServiceImpl();
    }

    @Bean
    public UpdateService updateService() {
        return new UpdateServiceImpl();
    }

    @Bean
    public RestService restService() {
        return new RestServiceImpl();
    }

}

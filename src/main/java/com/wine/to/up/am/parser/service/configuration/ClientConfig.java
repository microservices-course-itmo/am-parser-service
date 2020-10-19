package com.wine.to.up.am.parser.service.configuration;

import com.wine.to.up.am.parser.service.repository.WineRepository;
import com.wine.to.up.am.parser.service.service.AmClient;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.SearchService;
import com.wine.to.up.am.parser.service.service.impl.AmClientImpl;
import com.wine.to.up.am.parser.service.service.impl.AmServiceImpl;
import com.wine.to.up.am.parser.service.service.impl.SearchServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Bean(name = "amClientBean")
    public AmClient amClient() {
        return new AmClientImpl();
    }

    @Bean(name = "amServiceBean")
    public AmService amService() {
        return new AmServiceImpl();
    }

}

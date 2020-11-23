package com.wine.to.up.am.parser.service.configuration;

import com.wine.to.up.am.parser.service.components.AmServiceMetricsCollector;
import com.wine.to.up.am.parser.service.service.AmClient;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.impl.AmClientImpl;
import com.wine.to.up.am.parser.service.service.impl.AmServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public AmClient amClient() {
        return new AmClientImpl();
    }

    @Bean
    public AmService amService(AmClient amClient, AmServiceMetricsCollector amServiceMetricsCollector) {
        return new AmServiceImpl(amClient, amServiceMetricsCollector);
    }

}

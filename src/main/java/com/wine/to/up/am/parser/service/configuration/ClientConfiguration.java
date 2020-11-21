package com.wine.to.up.am.parser.service.configuration;

import com.wine.to.up.am.parser.service.service.AmClient;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.ProxyService;
import com.wine.to.up.am.parser.service.service.impl.AmClientImpl;
import com.wine.to.up.am.parser.service.service.impl.AmServiceImpl;
import com.wine.to.up.am.parser.service.service.impl.ProxyServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public AmClient amClient() {
        return new AmClientImpl();
    }

    @Bean
    public AmService amService(AmClient amClient, ProxyService proxyService) {
        return new AmServiceImpl(amClient, proxyService);
    }

    @Bean
    public ProxyService proxyService() {
        final ProxyService proxyService = new ProxyServiceImpl();
        proxyService.initProxy();
        return proxyService;
    }
}

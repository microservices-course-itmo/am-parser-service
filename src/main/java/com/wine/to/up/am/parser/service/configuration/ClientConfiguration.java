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
    public AmClient amClient(ProxyService proxyService) {
        return new AmClientImpl(proxyService);
    }

    @Bean
    public AmService amService(AmClient amClient) {
        return new AmServiceImpl(amClient);
    }

    @Bean
    public ProxyService proxyService() {
        return new ProxyServiceImpl();
    }
}

package com.wine.to.up.am.parser.service.configuration;

import com.wine.to.up.am.parser.service.components.AmServiceMetricsCollector;
import com.wine.to.up.am.parser.service.service.AmClient;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.ProxyClient;
import com.wine.to.up.am.parser.service.service.ProxyService;
import com.wine.to.up.am.parser.service.service.impl.AmClientImpl;
import com.wine.to.up.am.parser.service.service.impl.AmServiceImpl;
import com.wine.to.up.am.parser.service.service.impl.ProxyClientImpl;
import com.wine.to.up.am.parser.service.service.impl.ProxyServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public ProxyClient proxyClient() {
        return new ProxyClientImpl();
    }

    @Bean
    public ProxyService proxyService(ProxyClient proxyClient) {
        return new ProxyServiceImpl(proxyClient);
    }

    @Bean
    public AmClient amClient(ProxyService proxyService, AmServiceMetricsCollector amServiceMetricsCollector) {
        return new AmClientImpl(proxyService, amServiceMetricsCollector);
    }

    @Bean
    public AmService amService(AmClient amClient, AmServiceMetricsCollector amServiceMetricsCollector) {
        return new AmServiceImpl(amClient, amServiceMetricsCollector);
    }

}

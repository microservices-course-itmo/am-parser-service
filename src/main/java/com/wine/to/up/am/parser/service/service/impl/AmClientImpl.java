package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.service.AmClient;
import com.wine.to.up.am.parser.service.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Proxy;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Slf4j
public class AmClientImpl implements AmClient {

    @Value(value = "${am.site.base-url}")
    private String baseUrl;

    @Value(value = "${am.site.user-agent}")
    private String userAgent;

    @Value(value = "${am.site.referrer}")
    private String referrer;

    @Value(value = "${am.site.max-retries}")
    private Integer maxRetries;

    private final ProxyService proxyService;

    public AmClientImpl(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    private Document getPage(String url) {
        int attempt = 0;
        Proxy proxy = proxyService.nextProxy();
        while (attempt < maxRetries) {
            Document document = fetchPage(url, proxy);
            if (document != null) {
                return document;
            }
            proxy = proxyService.nextProxy();
            attempt ++;
        }
        log.error("Cannot get document by '{}' url in {} attempts", url, attempt);
        return null;
    }

    private Document fetchPage(String url, Proxy proxy) {
        try {
            return Jsoup
                    .connect(url)
                    .proxy(proxy)
                    .userAgent(userAgent)
                    .referrer(referrer)
                    .get();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Document getPage(Long page) {
        return getPage(baseUrl + "?page=" + page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document getMainPage() {
        return getPage(baseUrl);
    }
}

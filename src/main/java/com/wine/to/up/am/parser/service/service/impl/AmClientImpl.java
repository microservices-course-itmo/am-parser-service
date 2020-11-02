package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.service.AmClient;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public Document getPage(Long page) {
        return getPage(baseUrl + "?page=" + page);
    }

    /**
     * Получение страницы каталога
     * @param url url, который используется для получения страницы.
     * @return страницу каталога
     */
    private Document getPage(String url) {
        int attempt = 0;
        while (attempt < maxRetries) {
            Document document = fetchPage(url);
            if (document != null) {
                return document;
            }
            attempt ++;
        }
        log.error("Cannot get document by '{}' url in {} attempts", url, attempt);
        return null;
    }

    private Document fetchPage(String url) {
        try {
            return Jsoup
                    .connect(url)
                    .userAgent(userAgent)
                    .referrer(referrer)
                    .get();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document getMainPage() {
        return getPage(baseUrl);
    }
}

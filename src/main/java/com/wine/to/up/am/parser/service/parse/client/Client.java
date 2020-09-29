package com.wine.to.up.am.parser.service.parse.client;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Slf4j
@Setter
@Getter
public class Client {
    private final String baseUrl;
    private final String userAgent;

    public Client(String baseUrl, String userAgent) {
        this.baseUrl = baseUrl;
        this.userAgent = userAgent;
    }

    public Document getDocumentByPath(String path) {
        try {
            return Jsoup.connect(baseUrl + path)
                    .userAgent(userAgent).get();
        } catch (Exception ex) {
            log.error("Can't connect to site by url = {} and userAgent = {}", baseUrl, userAgent, ex);
            return null;
        }
    }
}

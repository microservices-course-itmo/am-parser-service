package com.wine.to.up.am.parser.service.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.to.up.am.parser.service.service.ProxyClient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : SSyrova
 * @since : 30.11.2020, пн
 **/
@Slf4j
public class ProxyClientImpl implements ProxyClient {

    @Value("${proxy.api-key}")
    private String apiKey;

    @Value("${proxy.api-url}")
    private String apiUrl;

    @Value(value = "${am.site.catalog-url}")
    private String catalogUrl;

    private final OkHttpClient client = new OkHttpClient();

    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public List<Proxy> getProxies() {
        HttpUrl.Builder builder = HttpUrl.parse(apiUrl).newBuilder();
        builder.addQueryParameter("key", apiKey);
        Request request = new Request.Builder().url(builder.build()).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() < 200 || response.code() > 204 || response.body() == null) {
                return Collections.emptyList();
            }
            ProxyResponse proxyResponse = mapper.readValue(response.body().bytes(), ProxyResponse.class);
            List<Proxy> proxyList = proxyResponse.data.stream()
                    .map(proxy -> new Proxy(Proxy.Type.HTTP,
                            new InetSocketAddress(proxy.ip, proxy.port)))
                    .filter(this::isProxyAlive)
                    .collect(Collectors.toList());
            if(!proxyList.isEmpty()) {
                log.info("Valid proxy count : {}", proxyList.size());
                return proxyList;
            } else {
                log.info("Trying again with proxy");
                return getProxies();
            }
        } catch (IOException e) {
            log.info("Cannot retrieve proxies: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private Boolean isProxyAlive(Proxy proxyAddress) {

        try {
            Jsoup.connect(catalogUrl).proxy(proxyAddress).timeout(20000).get();

            log.trace("{} OK", proxyAddress);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    static class ProxyResponse {
        List<ProxyJson> data;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    static class ProxyJson {
        String ip;
        Integer port;
    }
}

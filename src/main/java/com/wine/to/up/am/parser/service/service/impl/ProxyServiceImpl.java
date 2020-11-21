package com.wine.to.up.am.parser.service.service.impl;


import com.wine.to.up.am.parser.service.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.net.Proxy.Type.SOCKS;

@Slf4j
public class ProxyServiceImpl implements ProxyService {


    @Value(value = "${am.site.base-url}")
    private String baseUrl;

    @Value(value = "${am.site.timeout}")
    private int timeout;

    private CopyOnWriteArrayList<Proxy> proxyList;
    private Iterator<Proxy> iterator;

    private static Proxy convertProxy(String proxyAddress) {
        String[] addressParts = proxyAddress.split(":");
        return new java.net.Proxy(SOCKS, new InetSocketAddress(addressParts[0], Integer.parseInt(addressParts[1])));
    }

    private List<String> getAllProxies() {
        try {
            URL url = new URL("https://api.proxyscrape.com/?request=getproxies&proxytype=socks5&country=all");
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            List<String> proxies = new ArrayList<>();
            while ((inputLine = in.readLine()) != null) proxies.add(inputLine);
            in.close();
            return proxies;
        } catch (IOException e) {
            log.error("Cannot get proxies from external list", e);
            return Collections.emptyList();
        }
    }

    private Proxy getProxyIfAlive(String proxyAddress) {
        Proxy proxy = convertProxy(proxyAddress);

        try {
            Jsoup.connect(baseUrl).proxy(proxy).timeout(timeout).get();

            log.trace("{} OK", proxyAddress);
            return proxy;
        } catch (Exception e) {
            return null;
        }
    }

    @PostConstruct
    public void initProxy() {
        log.info("Getting proxies");
        List<Proxy> alive = new ArrayList<>();

        List<Future<Proxy>> futures;
        List<String> proxyAddresses = getAllProxies();
        ExecutorService threadPool = Executors.newFixedThreadPool(proxyAddresses.size());
        log.info("Loaded list of {} proxies. Checking", proxyAddresses.size());
        futures = proxyAddresses.stream().map(proxyAddress -> CompletableFuture.supplyAsync(() -> getProxyIfAlive(proxyAddress), threadPool)).collect(Collectors.toList());

        for (Future<Proxy> future : futures) {
            try {
                Proxy proxyResult = future.get();
                if (proxyResult != null) {
                    alive.add(proxyResult);
                }
            } catch (ExecutionException e) {
                log.error("An exception occurred while checking proxy asynchronously", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("Got {} suitable proxies", alive.size());

        proxyList = new CopyOnWriteArrayList<>(alive);
        iterator = proxyList.iterator();
    }

    public Proxy nextProxy() {
        if (!proxyList.isEmpty()) {
            if (iterator.hasNext()) {
                return iterator.next();
            } else {
                iterator = proxyList.iterator();
                return nextProxy();
            }
        } else {
            return Proxy.NO_PROXY;
        }
    }
}

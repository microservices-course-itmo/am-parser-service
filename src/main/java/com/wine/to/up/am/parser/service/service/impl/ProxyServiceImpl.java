package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.service.ProxyClient;
import com.wine.to.up.am.parser.service.service.ProxyService;

import javax.annotation.PostConstruct;
import java.net.Proxy;
import java.util.Iterator;
import java.util.List;

/**
 * @author : SSyrova
 * @since : 30.11.2020, пн
 **/
public class ProxyServiceImpl implements ProxyService {

    private final ProxyClient proxyClient;

    private List<Proxy> proxies;
    private Iterator<Proxy> iterator;

    public ProxyServiceImpl(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @PostConstruct
    public void initProxies() {
        proxies = proxyClient.getProxies();
        iterator = proxies.iterator();
    }

    @Override
    public Proxy getProxy() {
        if (proxies != null && !proxies.isEmpty()) {
            if (iterator == null || !iterator.hasNext()) {
                iterator = proxies.iterator();
            }
            return iterator.next();
        } else {
            initProxies();
            return getProxy();
        }
    }
}

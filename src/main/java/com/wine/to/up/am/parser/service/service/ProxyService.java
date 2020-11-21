package com.wine.to.up.am.parser.service.service;

import java.net.Proxy;

/**
 * @author : skorz
 * @since : 20.11.2020, пт
 **/
public interface ProxyService {

    void initProxy();

    Proxy nextProxy();
}

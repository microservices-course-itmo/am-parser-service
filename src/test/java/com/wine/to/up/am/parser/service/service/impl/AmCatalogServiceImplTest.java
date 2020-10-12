package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.service.AmCatalogService;
import com.wine.to.up.am.parser.service.service.AmClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author : SSyrova
 * @since : 05.10.2020, пн
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class AmCatalogServiceImplTest {

    @Autowired
    @Qualifier("amCatalogServiceImpl")
    private AmCatalogService amCatalogService;

    @Autowired
    @Qualifier("jsoupAmClientImpl")
    private AmClient amClient;

    @Test
    public void updateCatalog() {
        amCatalogService.updateCatalog(amCatalogService.getCatalog(amClient.getMainPage()));
    }
}
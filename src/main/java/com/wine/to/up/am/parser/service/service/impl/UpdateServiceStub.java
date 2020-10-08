package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.service.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Service
@Slf4j
public class UpdateServiceStub implements UpdateService {

    @Override
    public void updateDictionary() {
        log.info("update dict.");
    }

    @Override
    public void updateWines() {
        log.info("update wines.");
    }
}

package com.wine.to.up.am.parser.service.controller;

import com.wine.to.up.am.parser.service.service.RestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author : SSyrova
 * @since : 09.10.2020, пт
 **/
@RestController
@RequestMapping("/parser")
@Slf4j
@Api(value = "Wine controller", description = "Updating the information in the database")
public class WineController {

    @Autowired
    @Qualifier("restServiceImpl")
    private RestService restService;

    /**
     * Обновление справочника в базе данных
     */
    @ApiOperation(value = "Updating the dictionary in the database")
    @PostMapping("/dictionary")
    public void updateDictionary() {
        ZoneOffset zone = ZoneOffset.of("Z");
        LocalDateTime startDate = LocalDateTime.now();
        log.info("start UpdateDictionary method at " + startDate);
        restService.updateDictionary();
        LocalDateTime endDate = LocalDateTime.now();
        log.info("end UpdateDictionary method at " + endDate + " duration = " + (endDate.toEpochSecond(zone) - startDate.toEpochSecond(zone)) + " seconds");
    }

    @ApiOperation(value = "Updating the catalog in the database")
    @PostMapping("/wine")
    public void updateCatalog() {
        ZoneOffset zone = ZoneOffset.of("Z");
        LocalDateTime startDate = LocalDateTime.now();
        log.info("start UpdateCatalog method at " + startDate);
        restService.updateWines();
        LocalDateTime endDate = LocalDateTime.now();
        log.info("end UpdateCatalog method at " + endDate + " duration = " + (endDate.toEpochSecond(zone) - startDate.toEpochSecond(zone)) + " seconds");
    }

    @ApiOperation(value = "Updating all information in the database")
    @PostMapping("/all")
    public void updateAll() {
        ZoneOffset zone = ZoneOffset.of("Z");
        LocalDateTime startDate = LocalDateTime.now();
        log.info("start UpdateAll method at " + startDate);
        restService.updateAll();
        LocalDateTime endDate = LocalDateTime.now();
        log.info("end UpdateAll method at " + endDate + " duration = " + (endDate.toEpochSecond(zone) - startDate.toEpochSecond(zone)) + " seconds");
    }
}
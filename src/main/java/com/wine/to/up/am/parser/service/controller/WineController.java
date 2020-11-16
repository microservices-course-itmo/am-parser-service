package com.wine.to.up.am.parser.service.controller;

import com.wine.to.up.am.parser.service.service.RestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author : SSyrova
 * @since : 09.10.2020, пт
 **/
@RestController
@RequestMapping("/parser")
@Slf4j
@Api(value = "Wine controller")
public class WineController {

    private static final String DURATION = " duration= ";
    private static final String SECONDS = " seconds";

    @Resource
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
        log.info("end UpdateDictionary method at " + endDate + DURATION + (endDate.toEpochSecond(zone) - startDate.toEpochSecond(zone)) + SECONDS);
    }

    @ApiOperation(value = "Updating the catalog in the database")
    @PostMapping("/wine")
    public void updateCatalog() {
        ZoneOffset zone = ZoneOffset.of("Z");
        LocalDateTime startDate = LocalDateTime.now();
        log.info("start UpdateCatalog method at " + startDate);
        restService.updateWines();
        LocalDateTime endDate = LocalDateTime.now();
        log.info("end UpdateCatalog method at " + endDate + DURATION + (endDate.toEpochSecond(zone) - startDate.toEpochSecond(zone)) + SECONDS);
    }

    @ApiOperation(value = "Updating all information in the database")
    @PostMapping("/all")
    public void updateAll() {
        ZoneOffset zone = ZoneOffset.of("Z");
        LocalDateTime startDate = LocalDateTime.now();
        log.info("start UpdateAll method at " + startDate);
        restService.updateAll();
        LocalDateTime endDate = LocalDateTime.now();
        log.info("end UpdateAll method at " + endDate + DURATION + (endDate.toEpochSecond(zone) - startDate.toEpochSecond(zone)) + SECONDS);
    }

    @ApiOperation(value = "get all wines in file")
    @GetMapping("/file")
    public void readAsFile(HttpServletResponse httpServletResponse) throws IOException {
        restService.readAsFile(httpServletResponse);
    }

    @ApiOperation(value = "clean all database")
    @PostMapping("/clean")
    public void cleanDatabase() {
        restService.cleanDatabase();
    }
}
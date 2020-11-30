package com.wine.to.up.am.parser.service.controller;

import com.wine.to.up.am.parser.service.service.RestService;
import com.wine.to.up.am.parser.service.util.TrackExecutionTime;
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

/**
 * @author : SSyrova
 * @since : 09.10.2020, пт
 **/
@RestController
@RequestMapping("/parser")
@Slf4j
@Api(value = "Wine controller")
public class WineController {

    @Resource
    private RestService restService;

    /**
     * Обновление справочника в базе данных
     */
    @ApiOperation(value = "Updating the dictionary in the database")
    @PostMapping("/dictionary")
    @TrackExecutionTime
    public void updateDictionary() {
        restService.updateDictionary();
    }

    @ApiOperation(value = "Updating the catalog in the database")
    @PostMapping("/wine")
    @TrackExecutionTime
    public void updateCatalog() {
        restService.updateWines();
    }

    @ApiOperation(value = "Updating additional properties of wines in the database")
    @PostMapping("/wine/additional")
    @TrackExecutionTime
    public void updateAdditionalWineProps() {
        restService.updateAdditionalProps();
    }

    @ApiOperation(value = "Updating all information in the database")
    @PostMapping("/all")
    @TrackExecutionTime
    public void updateAll() {
        restService.updateAll();
    }

    @ApiOperation(value = "get all wines in file")
    @GetMapping("/file")
    @TrackExecutionTime
    public void readAsFile(HttpServletResponse httpServletResponse) throws IOException {
        restService.readAsFile(httpServletResponse);
    }

    @ApiOperation(value = "clean all database")
    @PostMapping("/clean")
    @TrackExecutionTime
    public void cleanDatabase() {
        restService.cleanDatabase();
    }
}
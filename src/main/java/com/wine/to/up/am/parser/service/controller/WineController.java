package com.wine.to.up.am.parser.service.controller;

import com.wine.to.up.am.parser.service.service.RestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : SSyrova
 * @since : 09.10.2020, пт
 **/
@RestController
@RequestMapping("/parser")
@Api(tags = {"Объекты главного экрана и баннеры"})
public class WineController {

    @Autowired
    @Qualifier("restServiceImpl")
    private RestService restService;

    /**
     * Обновление справочника в базе данных
     */
    @ApiOperation(value = "Обновление справочника в базе данных")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное выполнение"),
            @ApiResponse(code = 500, message = "Внутренная ошибка сервера")
    })
    @PostMapping("/dictionary")
    public void updateDictionary() {
        restService.updateDictionary();
    }

    @PostMapping("/wine")
    public void updateCatalog() {
        restService.updateWines();
    }
}

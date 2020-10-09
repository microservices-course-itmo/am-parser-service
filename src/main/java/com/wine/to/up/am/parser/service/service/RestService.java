package com.wine.to.up.am.parser.service.service;

import com.wine.to.up.am.parser.service.model.dto.WineDto;

import java.util.List;

/**
 * @author : SSyrova
 * @since : 07.10.2020, ср
 **/
public interface RestService {

    List<WineDto> findAllLessByRub(Double price);

    void updateDictionary();

    void updateWines();

    void updateAll();
}

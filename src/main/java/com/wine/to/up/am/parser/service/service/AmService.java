package com.wine.to.up.am.parser.service.service;

import com.wine.to.up.am.parser.service.model.dto.AmWine;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.model.dto.WineDto;

import java.util.List;

/**
 * @author : SSyrova
 * @since : 07.10.2020, ср
 **/
public interface AmService {

    List<WineDto> getWines();

    List<AmWine> getAmWines();

    Dictionary getDictionary();
}

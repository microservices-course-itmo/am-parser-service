package com.wine.to.up.am.parser.service.service;

import com.wine.to.up.am.parser.service.model.dto.AmWine;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;

import java.util.List;

/**
 * @author : SSyrova
 * @since : 07.10.2020, ср
 **/
public interface AmService {

    /**
     * Метод получения вин путем получения HTML страниц и их парсинга.
     * @return Список вин в "сыром" виде(вместо атрибутов - их id в справочнике).
     */
    List<AmWine> getAmWines();

    /**
     * Метод получения справочной информации в виде словаря.
     * @return Словарь справочной информации.
     */
    Dictionary getDictionary();
}

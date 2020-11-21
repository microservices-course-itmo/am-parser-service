package com.wine.to.up.am.parser.service.service;

import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.service.impl.AmServiceImpl;

/**
 * @author : SSyrova
 * @since : 07.10.2020, ср
 **/
public interface AmService {

    /**
     * Метод получения вин путем получения HTML страниц и их парсинга.
     */
    void parseAmWines(AmServiceImpl.OnPageParseCallback callback);

    /**
     * Метод получения справочной информации в виде словаря.
     * @return Словарь справочной информации.
     */
    Dictionary getDictionary();
}

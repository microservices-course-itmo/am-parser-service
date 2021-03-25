package com.wine.to.up.am.parser.service.service;

import com.wine.to.up.am.parser.service.model.dto.AmWine;

import java.util.List;

/**
 * @author : SSyrova
 * @since : 07.10.2020, ср
 **/
public interface UpdateService {

    /**
     * Обновление справочной информации.
     */
    void updateDictionary();

    /**
     * Получение вин и обновление информации в БД(добавление вина или изменение полей вина)
     */
    void updateWines();

    void updateWines(List<AmWine> wines);

    /**
     * Обновление дополнительных атрибутов, получаемых со страниц вина.
     */
    void updateAdditionalProps();

    void cleanDatabase();
}

package com.wine.to.up.am.parser.service.service;

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

    /**
     * Обновление дополнительных атрибутов, получаемых со страниц вина.
     */
    void updateAdditionalProps();

    void cleanDatabase();
}

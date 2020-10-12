package com.wine.to.up.am.parser.service.service;

import com.wine.to.up.am.parser.service.model.dto.WineDto;

import java.util.List;

/**
 * @author : SSyrova
 * @since : 04.10.2020, вс
 **/
public interface AmService {

    /**
     * Метод будет вызываться по расписанию для обновления каталога и вин.
     */
    void updateDatabase();

}

package com.wine.to.up.am.parser.service.service;

import com.wine.to.up.am.parser.service.domain.entity.Wine;
import com.wine.to.up.am.parser.service.model.dto.WineDto;

import java.util.List;

/**
 * @author : SSyrova
 * @since : 07.10.2020, ср
 **/
public interface SearchService {

    /**
     * Поиск вин,у которых цена меньше указанной.
     * @param price Пороговая цена.
     * @return Список DTO.
     */
    List<WineDto> findAllLessByRub(Double price);

    /**
     * Получение всех вин из БД.
     * @return Список всех вин в формате DTO.
     */
    List<WineDto> findAll();

    Wine nWine(Integer n);
}

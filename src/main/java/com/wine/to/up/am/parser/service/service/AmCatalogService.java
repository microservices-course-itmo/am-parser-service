package com.wine.to.up.am.parser.service.service;

import com.wine.to.up.am.parser.service.model.dto.Catalog;
import org.jsoup.nodes.Document;

/**
 * @author : SSyrova
 * @since : 04.10.2020, вс
 **/
public interface AmCatalogService {

    Catalog getCatalog(Document document);

    /**
     * Метод должен обновлять данные в базе данных или добавлять новые.
     * Сравнение сущностей производится по importId.
     * В лог должно выводится сколько сущностей обновилось, удалилось или создалось
     *
     * @param catalog винный каталог
     */
    void updateCatalog(Catalog catalog);
}

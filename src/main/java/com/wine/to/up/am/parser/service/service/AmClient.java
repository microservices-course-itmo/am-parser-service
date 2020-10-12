package com.wine.to.up.am.parser.service.service;

import org.jsoup.nodes.Document;

/**
 * @author : SSyrova
 * @since : 29.09.2020, вт
 **/
public interface AmClient {

    /**
     * Получение страницы каталога
     *
     * @return страницу каталога
     */
    Document getPage(Long page);

    /**
     * Получение главной страницы каталога.
     * Предполагается использовать этот метод для получения информации о том, сколько
     * всего винных страниц
     *
     * @return главная страница каталога
     */
    Document getMainPage();
}


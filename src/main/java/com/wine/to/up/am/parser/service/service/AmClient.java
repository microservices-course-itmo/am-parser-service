package com.wine.to.up.am.parser.service.service;

import org.jsoup.nodes.Document;

import java.net.Proxy;

/**
 * @author : SSyrova
 * @since : 07.10.2020, ср
 **/
public interface AmClient {

    /**
     * Получение страницы каталога
     * @param page Номер получаемой страницы каталога.
     * @return страницу каталога
     */
    Document getPage(Long page);

    Document getPage(Long page, Proxy proxy);

    /**
     * Получение главной страницы каталога.
     * Предполагается использовать этот метод для получения информации о том, сколько
     * всего винных страниц
     *
     * @return главная страница каталога
     */
    Document getMainPage();
}

package com.wine.to.up.am.parser.service.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Getter
@Setter
@Builder
@ApiModel(description = "All details about the WineDTO (DTO)")
public class WineDto {

    /**
     * Название вина.
     */
    private String name;

    /**
     * Ссылка на изображение.
     */
    private String picture;

    /**
     * Цвет вина.
     */
    private String color;

    /**
     * Сахаристость вина.
     */
    private String sugar;

    /**
     * Страна производства.
     */
    private String country;

    /**
     * Крепость вина.
     */
    private Double alco;

    /**
     * Объем бутылки.
     */
    private Double value;

    /**
     * Цена за бутылку вина.
     */
    private Double price;

    /**
     * Старая цена за бутылку вина.
     */
    private Double oldPrice;

    /**
     * Ссылка на винную позицию.
     */
    private String link;

    /**
     * Регион производства бутылки вина.
     */
    private String region;

    /**
     * Производитель вина.
     */
    private String producer;

    /**
     * Описание вина.
     */
    private String description;

    /**
     * Описание аромата вина.
     */
    private String flavor;

    /**
     * Описание гастрономических сочетаний вина.
     */
    private String gastronomy;

    /**
     * Описание вкуса вина.
     */
    private String taste;

    /**
     * Рейтинг вина на сайте.
     */
    private Double rating;

    /**
     * Бренд вина.
     */
    private String brand;

    /**
     * Сорта винограда.
     */
    private List<String> grapes;
}

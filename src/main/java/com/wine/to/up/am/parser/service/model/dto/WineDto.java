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
     * Сорта винограда.
     */
    private List<String> grapes;
}

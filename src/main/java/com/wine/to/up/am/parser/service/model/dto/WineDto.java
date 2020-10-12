package com.wine.to.up.am.parser.service.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    private String name;

    private String picture;

    private String color;

    private String sugar;

    private String country;

    private Double alco;

    private Double value;

    private List<String> grapes;
}

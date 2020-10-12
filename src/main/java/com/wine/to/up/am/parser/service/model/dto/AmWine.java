package com.wine.to.up.am.parser.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author : SSyrova
 * @since : 29.09.2020, вт
 **/

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "All details about the AmWine (DTO)")
public class AmWine {

    private String id;
    private Long sort;
    private String name;
    private Props props;
    @JsonProperty("preview_picture")
    private String pictureUrl;


    @Getter
    @Setter
    @NoArgsConstructor
    public class Props {
        private Long color;
        private Long sugar;
        private String country;
        private Double alco;
        private Double value;
        @JsonProperty("grape_sort")
        private List<String> grapes;

    }
}

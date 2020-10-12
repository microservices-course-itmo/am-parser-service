package com.wine.to.up.am.parser.service.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "colors")
@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "All details about the Color")
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated color ID")
    private Long id;

    @ApiModelProperty(notes = "The import ID")
    @Column(name = "import_id")
    private String importId;

    @ApiModelProperty(notes = "The name of the color")
    @Column(name = "name")
    private String name;

    public Color(String importId, String name) {
        this.importId = importId;
        this.name = name;
    }
}

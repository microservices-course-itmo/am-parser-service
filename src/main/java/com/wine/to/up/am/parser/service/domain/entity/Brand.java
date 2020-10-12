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
@Table(name = "brands")
@Setter
@Getter
@NoArgsConstructor
@ApiModel(description = "All details about the Brand")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated brand ID")
    private long id;

    @ApiModelProperty(notes = "The import ID")
    @Column(name = "import_id")
    private String importId;

    @ApiModelProperty(notes = "The name of the brand")
    @Column(name = "name")
    private String name;

    public Brand(String importId, String name) {
        this.importId = importId;
        this.name = name;
    }
}

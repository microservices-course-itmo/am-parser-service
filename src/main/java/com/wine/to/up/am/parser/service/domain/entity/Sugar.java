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
@Table(name = "sugar")
@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "All details about the Sugar")
public class Sugar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated sugar ID")
    private Long id;

    @Column(name = "import_id")
    @ApiModelProperty(notes = "The import ID")
    private String importId;

    @ApiModelProperty(notes = "The name of the sugar")
    private String name;

    public Sugar(String importId, String name) {
        this.importId = importId;
        this.name = name;
    }
}

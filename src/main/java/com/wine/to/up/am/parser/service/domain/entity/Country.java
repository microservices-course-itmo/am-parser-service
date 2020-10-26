package com.wine.to.up.am.parser.service.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Table(name = "countries")
@Setter
@Getter
@NoArgsConstructor
@ApiModel(description = "All details about the Country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated country ID")
    private long id;

    @Column(name = "import_id")
    @ApiModelProperty(notes = "The import ID")
    private String importId;

    @Column(name = "name")
    @ApiModelProperty(notes = "The name of the country")
    private String name;

    @Column(name = "actual")
    private boolean actual;

    @Column(name = "date_rec")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRec;

    public Country(String importId, String name) {
        this.importId = importId;
        this.name = name;
    }

    public Country(String importId, String name, boolean actual, Date dateRec) {
        this.importId = importId;
        this.name = name;
        this.actual = actual;
        this.dateRec = dateRec;
    }
}

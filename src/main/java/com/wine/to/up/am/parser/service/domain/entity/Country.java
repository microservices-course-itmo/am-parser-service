package com.wine.to.up.am.parser.service.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "countries")
@Setter
@Getter
@NoArgsConstructor
public class Country {
    @Id
    @GeneratedValue
    @Column(name = "country_id")
    private long countryID;
    @Column(name = "import_id")
    private long importID;
    @Column(name = "country_name")
    private String countryName;

    public Country(long importID, String countryName) {
        this.importID = importID;
        this.countryName = countryName;
    }
}

package com.wine.to.up.am.parser.service.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "wines")
@Setter
@Getter
@NoArgsConstructor
public class Wine {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Column(name = "import_id")
    private String importId;

    @Column(name = "picture_url")
    private String pictureUrl;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private Brand brand;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private Country country;

    private double volume;

    private double strength;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private Color color;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private Sugar sugar;

    @ManyToMany
    private List<Grape> grapes;

    private double price;

    public Wine(String importId,
                String pictureUrl,
                Brand brand,
                Country country,
                double volume,
                double strength,
                Color color,
                Sugar sugar,
                List<Grape> grapes,
                double price) {
        this.importId = importId;
        this.pictureUrl = pictureUrl;
        this.brand = brand;
        this.country = country;
        this.volume = volume;
        this.strength = strength;
        this.color = color;
        this.sugar = sugar;
        this.grapes = grapes;
        this.price = price;
    }
}
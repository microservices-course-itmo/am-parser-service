package com.wine.to.up.am.parser.service.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    private String name;

    @Column(name = "picture_url")
    private String pictureUrl;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Country country;

    private double volume;

    private double strength;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Color color;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Sugar sugar;

    @ManyToMany(fetch = FetchType.EAGER)
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
package com.wine.to.up.am.parser.service.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "wines")
@Setter
@Getter
@NoArgsConstructor
@ApiModel(description = "All details about the Wine")
public class Wine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "The database generated wine ID")
    private long id;

    @Column(name = "import_id")
    @ApiModelProperty(notes = "The import ID")
    private String importId;

    @ApiModelProperty(notes = "The name of the wine")
    private String name;

    @Column(name = "picture_url")
    @ApiModelProperty(notes = "The URL of the picture")
    private String pictureUrl;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    @ApiModelProperty(notes = "The brand")
    private Brand brand;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    @ApiModelProperty(notes = "The country")
    private Country country;

    @ApiModelProperty(notes = "Volume of the bottle")
    private double volume;

    @ApiModelProperty(notes = "Percent of the alcohol")
    private double strength;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Color color;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Sugar sugar;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Grape> grapes;

    @ApiModelProperty(notes = "The price of the bottle")
    private double price;

    @Column(name = "actual")
    private Boolean actual;

    @Column(name = "date_rec")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRec;

    public static Builder newBuilder() {
        return new Wine().new Builder();
    }

    public class Builder {

        private Builder() {}

        public Builder bSetImportId(String importId) {
            Wine.this.importId = importId;
            return this;
        }

        public Builder bSetName(String name) {
            Wine.this.name = name;
            return this;
        }

        public Builder bSetPictureUrl(String pictureUrl) {
            Wine.this.pictureUrl = pictureUrl;
            return this;
        }

        public Builder bSetBrand(Brand brand) {
            Wine.this.brand = brand;
            return this;
        }

        public Builder bSetCountry(Country country) {
            Wine.this.country = country;
            return this;
        }

        public Builder bSetVolume(double volume) {
            Wine.this.volume = volume;
            return this;
        }

        public Builder bSetStrength(double strength) {
            Wine.this.strength = strength;
            return this;
        }

        public Builder bSetColor(Color color) {
            Wine.this.color = color;
            return this;
        }

        public Builder bSetSugar(Sugar sugar) {
            Wine.this.sugar = sugar;
            return this;
        }

        public Builder bSetGrapes(List<Grape> grapes) {
            Wine.this.grapes = grapes;
            return this;
        }

        public Builder bSetPrice(double price) {
            Wine.this.price = price;
            return this;
        }

        public Builder bSetActual(boolean actual) {
            Wine.this.actual = actual;
            return this;
        }

        public Builder bSetDateRec(Date dateRec) {
            Wine.this.dateRec = dateRec;
            return this;
        }

        public Wine build() {
            return Wine.this;
        }

    }

}
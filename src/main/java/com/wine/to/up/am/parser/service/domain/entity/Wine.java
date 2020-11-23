package com.wine.to.up.am.parser.service.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "All details about the Wine")
public class Wine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "The database generated wine ID")
    private Long id;

    @Column(name = "import_id")
    @ApiModelProperty(notes = "The import ID")
    private String importId;

    @ApiModelProperty(notes = "The name of the wine")
    @Column(nullable = false)
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
    @Column(nullable = true)
    private Double volume;

    @ApiModelProperty(notes = "Percent of the alcohol")
    @Column(nullable = true)
    private Double strength;

    @Column(nullable = true)
    private String link;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String taste;

    @Column(nullable = true)
    private String flavor;

    @Column(nullable = true)
    private String gastronomy;

    @Column(nullable = true)
    private Double rating;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Color color;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Sugar sugar;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Region region;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Producer producer;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Grape> grapes;

    @ApiModelProperty(notes = "The price of the bottle")
    @Column(nullable = true)
    private Double price;

    @Column(nullable = true, name = "old_price")
    private Double oldPrice;

    @Column(nullable = false)
    private Boolean actual;

    @Column(name = "date_rec")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRec;

}
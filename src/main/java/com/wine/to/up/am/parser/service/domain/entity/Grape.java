package com.wine.to.up.am.parser.service.domain.entity;

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
@Table(name = "grapes")
@Setter
@Getter
@NoArgsConstructor
public class Grape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "import_id")
    private String importId;

    @Column(name = "name")
    private String name;

    public Grape(String importId, String name) {
        this.importId = importId;
        this.name = name;
    }
}

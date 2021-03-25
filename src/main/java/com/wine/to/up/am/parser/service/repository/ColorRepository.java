package com.wine.to.up.am.parser.service.repository;

import com.wine.to.up.am.parser.service.domain.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : SSyrova
 * @since : 04.10.2020, вс
 **/
@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {

    Color findByImportId(String importId);
}

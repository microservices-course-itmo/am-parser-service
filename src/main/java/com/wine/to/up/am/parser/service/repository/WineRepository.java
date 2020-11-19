package com.wine.to.up.am.parser.service.repository;

import com.wine.to.up.am.parser.service.domain.entity.Wine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author : SSyrova
 * @since : 29.09.2020, вт
 **/
@Repository
public interface WineRepository extends JpaRepository<Wine, Long> {

    List<Wine> findAllByPriceLessThan(Double price);

    Wine findByImportId(String importId);

    List<Wine> findAllByDateRecIsNot(Date date);
}

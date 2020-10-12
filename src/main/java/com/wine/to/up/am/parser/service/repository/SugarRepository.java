package com.wine.to.up.am.parser.service.repository;

import com.wine.to.up.am.parser.service.domain.entity.Sugar;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : SSyrova
 * @since : 04.10.2020, вс
 **/
@Repository
public interface SugarRepository extends CrudRepository<Sugar, Long> {

    Sugar findByImportId(String importId);

}

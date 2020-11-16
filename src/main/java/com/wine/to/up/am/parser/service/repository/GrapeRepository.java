package com.wine.to.up.am.parser.service.repository;

import com.wine.to.up.am.parser.service.domain.entity.Grape;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrapeRepository extends CrudRepository<Grape, Long> {

    Grape findByImportId(String importId);

    List<Grape> findAllByImportIdIn(List<String> importIds);
}

package com.wine.to.up.am.parser.service.repository;

import com.wine.to.up.am.parser.service.domain.entity.Grape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrapeRepository extends JpaRepository<Grape, Long> {

    List<Grape> findAllByImportIdIn(List<String> importIds);
}

package com.wine.to.up.am.parser.service.repository;

import com.wine.to.up.am.parser.service.domain.entity.Brand;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends CrudRepository<Brand, Long> {

    Brand findByImportId(String importId);

    Brand findByName(String name);
}

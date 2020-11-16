package com.wine.to.up.am.parser.service.domain.entity;

import java.util.Date;

/**
 * @author : skorz
 * @since : 16.11.2020, пн
 **/
public interface DictionaryValue {

    String getImportId();

    void setName(String name);

    void setDateRec(Date dateRec);

    void setActual(Boolean actual);

}

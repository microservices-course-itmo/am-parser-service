package com.wine.to.up.am.parser.service.logging;

import com.wine.to.up.commonlib.logging.NotableEvent;

public enum AmServiceNotableEvents implements NotableEvent {

    I_WINES_PAGE_PARSED("Successfully parsed a page with wines"),
    I_WINE_DETAILS_PARSED("Successfully parsed a wine's details page"),
    W_WINE_PAGE_PARSING_FAILED("Failed to parse a page with wines. URL: {}"),
    W_WINE_DETAILS_PARSING_FAILED("Failed to parse a wine's details page. URL: {}"),
    W_WINE_ATTRIBUTE_ABSENT("A wine doesn't have one of its expected attributes. Attribute name: {}. URL: {}");

    private final String template;

    AmServiceNotableEvents(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    @Override
    public String getName() {
        return name();
    }


}

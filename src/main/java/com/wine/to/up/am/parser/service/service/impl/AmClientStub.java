package com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.service.AmClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Component
public class AmClientStub implements AmClient {

    private static final String EMPTY_DOC =
            "<!DOCTYPE HTML>" +
                    "<html lang=\"en\">" +
                    "<head>" +
                    "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">" +
                    "<title>Title Goes Here</title>" +
                    "</head>" +
                    "<body>" +
                    "<p>This is my web page</p>" +
                    "</body>" +
                    "</html>";

    @Override
    public Document getPage(Long page) {
        return Jsoup.parse(EMPTY_DOC);
    }

    @Override
    public Document getMainPage() {
        return Jsoup.parse(EMPTY_DOC);
    }
}

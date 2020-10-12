package com.wine.to.up.am.parser.service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.to.up.am.parser.service.model.dto.Catalog;
import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.service.AmParserService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class AmParserServiceImpl implements AmParserService {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Pattern productsPattern = Pattern.compile(".*window\\.products\\s*=\\s*(\\[.*]);");

    private static final Pattern totalCountPattern = Pattern.compile(".*window\\.productsTotalCount\\s*=\\s*(\\d*);");

    private static final Pattern perPageCountPattern = Pattern.compile(".*window\\.productsPerServerPage\\s*=\\s*(\\d*);");

    private static final Pattern catalogProps = Pattern.compile(".*window\\.catalogProps\\s*=\\s*(\\{.*});");

    public AmParserServiceImpl() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public List<WineDto> parsePage(Document document) {
        Elements elements = document.getElementsByTag("script");
        String jsonStr = "";
        for (Element element : elements) {
            if (element.data().contains("window.products")) {
                jsonStr = getValue(productsPattern, element);
                break;
            }
        }
        try {
            jsonStr = jsonStr != null ? jsonStr.replaceAll("'", "\"") : "";
            jsonStr = jsonStr.replaceAll("\\s", " ");
            return mapper.readValue(jsonStr, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Can't parse document", e);
            return new ArrayList<>();
        }
    }

    @Override
    public Long getCatalogPagesAmount(Document document) {
        Elements elements = document.getElementsByTag("script");
        String totalCount = "";
        String perPageCount = "";
        for (Element element : elements) {
            if (element.data().contains("window.products")) {
                totalCount = getValue(totalCountPattern, element);
                perPageCount = getValue(perPageCountPattern, element);
            }
        }
        if (!StringUtils.hasText(totalCount) || !StringUtils.hasText(perPageCount)) {
            return -1L;
        }
        try {
            Long longTotalCount = Long.parseLong(totalCount);
            Long longPerPageCount = Long.parseLong(perPageCount);
            return (longTotalCount / longPerPageCount)
                    + (longTotalCount % longPerPageCount == 0 ? 0 : 1);
        } catch (NumberFormatException e) {
            log.error("Cannot parse total object count or per page count: {}", e.getMessage());
            return -1L;
        }
    }

    @Override
    public Catalog parseCatalog(Document document) {
        Elements elements = document.getElementsByTag("script");
        String jsonStr = "";
        for (Element element : elements) {
            if (element.data().contains("window.catalogProps")) {
                jsonStr = getValue(catalogProps, element);
                break;
            }
        }
        try {
            jsonStr = jsonStr != null ? jsonStr.replaceAll("'", "\"") : "";
            jsonStr = jsonStr.replaceAll("\\s", " ");
            return mapper.readValue(jsonStr, Catalog.class);
        } catch (JsonProcessingException e) {
            log.error("Can't parse document", e);
            return null;
        }
    }

    private String getValue(Pattern pattern, Element element) {
        Matcher matcher = pattern.matcher(element.data());
        return matcher.find() ? matcher.group(1) : null;
    }
}

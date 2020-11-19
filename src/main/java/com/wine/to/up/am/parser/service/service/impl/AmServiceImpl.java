package com.wine.to.up.am.parser.service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.to.up.am.parser.service.model.dto.AmWine;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.service.AmClient;
import com.wine.to.up.am.parser.service.service.AmService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Service
@Slf4j
public class AmServiceImpl implements AmService {

    private final AmClient client;

    private static final String DICT_NAME = "catalogProps";

    private static final String WINDOW_PATTERN_START = ".*window\\.";

    private static final Pattern DICT_PATTERN = Pattern.compile(WINDOW_PATTERN_START + DICT_NAME + "\\s*=\\s*(\\{.*});");

    private static final String TOTAL_COUNT_NAME = "productsTotalCount";

    private static final Pattern TOTAL_COUNT_PATTERN = Pattern.compile(WINDOW_PATTERN_START + TOTAL_COUNT_NAME + "\\s*=\\s*(\\d*);");

    private static final String PER_PAGE_COUNT_NAME = "productsPerServerPage";

    private static final Pattern PER_PAGE_COUNT_PATTERN = Pattern.compile(WINDOW_PATTERN_START + PER_PAGE_COUNT_NAME + "\\s*=\\s*(\\d*);");

    private static final String PROD_NAME = "products";

    private static final Pattern PROD_PATTERN = Pattern.compile(WINDOW_PATTERN_START + PROD_NAME + "\\s*=\\s*(\\[.*]);");

    private static final ObjectMapper mapper = new ObjectMapper();

    public AmServiceImpl(AmClient client) {
        this.client = client;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WineDto> getWines() {
        Dictionary dictionary = getDictionary();
        List<AmWine> wines = getAmWines();
        List<WineDto> wineDtos = new ArrayList<>();
        for (AmWine amWine : wines) {
            WineDto wineDto = WineDto.builder()
                    .name(amWine.getName())
                    .picture(amWine.getPictureUrl())
                    .alco(amWine.getProps().getAlco())
                    .color(dictionary.getColors().get(amWine.getProps().getColor().toString()).getValue())
                    .country(dictionary.getCountries().get(amWine.getProps().getCountry()).getValue())
                    .grapes(amWine.getProps().getGrapes().stream().map(e -> dictionary.getGrapes().get(e).getValue()).collect(Collectors.toList()))
                    .sugar(dictionary.getSugars().get(amWine.getProps().getSugar().toString()).getValue())
                    .value(amWine.getProps().getValue())
                    .build();
            wineDtos.add(wineDto);
        }
        return wineDtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AmWine> getAmWines() {
        Long pages = getCatalogPagesAmount();
        log.info("The catalog contains {} pages", pages);
        List<AmWine> amWines = new CopyOnWriteArrayList<>();
        long page = 1L;

        int parseAttemptsCount = 0;
        int successfulParseCount = 0;
        final boolean[] pagesProcessed = new boolean[pages.intValue()];
        final boolean[] pagesWithParsedWines = new boolean[pages.intValue()];

        while (page <= pages) {
            parseAttemptsCount++;
            long pageCopy = page;
            List<AmWine> newWines = getAmWines(page);
            page++;
            amWines.addAll(newWines);
            pagesProcessed[(int) pageCopy - 1] = true;
            if (!newWines.isEmpty()) {
                successfulParseCount++;
                pagesWithParsedWines[(int) pageCopy - 1] = true;
            }
        }

        StringBuilder lostPagesLog = new StringBuilder("Unprocessed pages: ");
        for (int i = 0; i < pages; i++) {
            if (!pagesProcessed[i]) {
                lostPagesLog.append(i + 1).append(", ");
            }
        }
        log.info(lostPagesLog.toString());
        StringBuilder zeroParsePagesLog = new StringBuilder("Pages from which zero wines were parsed: ");
        for (int i = 0; i < pages; i++) {
            if (!pagesWithParsedWines[i]) {
                zeroParsePagesLog.append(i + 1).append(", ");
            }
        }
        log.info(zeroParsePagesLog.toString());
        if (successfulParseCount == parseAttemptsCount) {
            log.info("All {} pages parsed successfully.", successfulParseCount);
        } else {
            log.info("Wines were successfully parsed from {} out of {} pages.", successfulParseCount, parseAttemptsCount);
        }
        return amWines;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dictionary getDictionary() {
        Dictionary result;
        final Document document = client.getMainPage();
        String rawDictionary = getRawValue(document, DICT_NAME, DICT_PATTERN);
        try {
            result = rawDictionary != null ? mapper.readValue(rawDictionary, Dictionary.class) : new Dictionary();
        } catch (JsonProcessingException e) {
            log.error("Cannot parse dictionary with error: {}", e.getMessage());
            result = new Dictionary();
        }
        return result;
    }

    /**
     * Получение списка вин в "сыром" виде() с заданной страницы.
     *
     * @param page Номер страницы, с которой мы парсим и получаем вина.
     * @return Список вин.
     */
    private List<AmWine> getAmWines(Long page) {
        final Document document = client.getPage(page);
        if (document == null) {
            return Collections.emptyList();
        }
        String rawWines = getRawValue(document, PROD_NAME, PROD_PATTERN);
        try {
            return rawWines != null ?
                    mapper.readValue(rawWines, new TypeReference<>() {
                    }) :
                    Collections.emptyList();
        } catch (JsonProcessingException e) {
            log.error("Cannot parse wines with error: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Получение информации о количестве страниц в каталоге.
     *
     * @return Количество страниц каталога.
     */
    private Long getCatalogPagesAmount() {
        final Document document = client.getMainPage();
        String rawTotalCount = getRawValue(document, TOTAL_COUNT_NAME, TOTAL_COUNT_PATTERN);
        String rawPerPageCount = getRawValue(document, PER_PAGE_COUNT_NAME, PER_PAGE_COUNT_PATTERN);
        if (!StringUtils.hasText(rawTotalCount) || !StringUtils.hasText(rawPerPageCount)) {
            log.error("Cannot get total page count or per page count");
            return -1L;
        }
        try {
            Long totalCount = Long.parseLong(rawTotalCount);
            Long perPageCount = Long.parseLong(rawPerPageCount);
            return (totalCount / perPageCount)
                    + (totalCount % perPageCount == 0 ? 0 : 1);
        } catch (NumberFormatException e) {
            log.error("Cannot parse string to long with error: {}", e.getMessage());
            return -1L;
        }
    }

    /**
     * Получение нужной информации из HTML в виде строки.
     * @param document Документ, в котором ведется поиск.
     * @param elementName Имя HTML элемента, из которого извлекается информация.
     * @param pattern Регулярное выражение, по которому ведется поиск.
     * @return Необходимая информация(справочник или каталог вина).
     */
    private String getRawValue(Document document, String elementName, Pattern pattern) {
        if (document == null) {
            return null;
        }
        Elements elements = document.getElementsByTag("script");
        for (Element element : elements) {
            if (element.data().contains(elementName)) {
                Matcher matcher = pattern.matcher(element.data());
                String value = matcher.find() ? matcher.group(1) : null;
                if (value != null) {
                    value = value.replace("'", "\"");
                    value = value.replaceAll("\\s", " ");
                    value = value.trim();
                }
                return value;
            }
        }
        return null;
    }
}

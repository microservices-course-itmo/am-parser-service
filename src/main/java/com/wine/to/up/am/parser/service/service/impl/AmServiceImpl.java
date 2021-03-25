package com.wine.to.up.am.parser.service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.to.up.am.parser.service.components.AmServiceMetricsCollector;
import com.wine.to.up.am.parser.service.model.dto.AdditionalProps;
import com.wine.to.up.am.parser.service.logging.AmServiceNotableEvents;
import com.wine.to.up.am.parser.service.model.dto.AmWine;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.service.AmClient;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
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

    private final AmServiceMetricsCollector metricsCollector;

    @InjectEventLogger
    private EventLogger eventLogger;

    @Value(value = "${am.site.base-url}")
    private String baseUrl;

    private static final String RATING_SCORE = "rating__score";

    private static final String FLAVOR = "Аромат";

    private static final String GASTRONOMY = "Гастроном";

    private static final String TASTE = "Вкус";

    private static final String DEGUSTATION = "Дегустационные характеристики";

    private static final String DESCRIPTION = "Дегустационные характеристики";

    private static final String WINE_PROPERTY = "about-wine__block col-md-4";

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

    public AmServiceImpl(AmClient client, AmServiceMetricsCollector metricsCollector) {
        this.client = client;
        this.metricsCollector = metricsCollector;
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

        Long lastParse = null;

        while (page <= pages) {
            metricsCollector.countParsingStart();
            metricsCollector.incParsingInProgress();
            parseAttemptsCount++;
            long pageCopy = page;
            List<AmWine> newWines = getAmWines(page);
            metricsCollector.parsedWinesSuccess(newWines.size());
            metricsCollector.winesParsedUnsuccessful(18 - newWines.size());
            metricsCollector.percentageOfUnsuccessfullyParsedWines(newWines.size() / 18);
            page++;
            amWines.addAll(newWines);
            pagesProcessed[(int) pageCopy - 1] = true;
            if (!newWines.isEmpty()) {
                successfulParseCount++;
                metricsCollector.countParsingComplete("SUCCESS");
                eventLogger.info(AmServiceNotableEvents.I_WINES_PAGE_PARSED, pageCopy);
                pagesWithParsedWines[(int) pageCopy - 1] = true;
                long currentParse = System.nanoTime();
                if(lastParse != null) {
                    metricsCollector.countTimeSinceLastParsing(currentParse - lastParse);
                }
                lastParse = currentParse;
            } else {
                metricsCollector.countParsingComplete("FAILED");
                eventLogger.warn(AmServiceNotableEvents.W_WINE_PAGE_PARSING_FAILED, pageCopy, baseUrl + "?page=" + pageCopy);
            }
            metricsCollector.decParsingInProgress();
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

    @Override
    public AdditionalProps getAdditionalProps(String link) {
        long fetchStart = System.nanoTime();
        Document page = client.getPageByUrl(link);
        long fetchEnd = System.nanoTime();
        metricsCollector.timeWineDetailsFetchingDuration(fetchEnd - fetchStart);
        if(page == null) {
            eventLogger.info(AmServiceNotableEvents.W_WINE_DETAILS_PARSING_FAILED, link);
        }
        return parseAdditionalProps(page);
    }

    private Double parseRating(Document page) {
        return Double.parseDouble(page.getElementsByClass(RATING_SCORE).get(0).text());
    }

    private AdditionalProps parseAdditionalProps(Document page) {
        AdditionalProps props = new AdditionalProps();
        if (page == null) {
            return null;
        }

        long parseStart = System.nanoTime();
        props.setRating(parseRating(page));
        Elements properties = page.getElementsByClass(WINE_PROPERTY);
        for (Element prop : properties) {
            handleProp(prop, props);
        }
        long parseEnd = System.nanoTime();
        metricsCollector.timeWineDetailsParsingDuration(parseEnd - parseStart);
        return props;
    }

    private void handleProp(Element prop, AdditionalProps props) {
        String title = prop.getElementsByTag("div").get(1).text();
        String body = prop.getElementsByTag("p").get(0).text();
        boolean isOtherVersion = false;
        if (title.contains(DEGUSTATION)) {
            isOtherVersion = true;
        }
        if (title.contains(DESCRIPTION)) {
            props.setDescription(body);
        }
        if (title.contains(TASTE) && !isOtherVersion) {
            props.setTaste(body);
        }
        if (title.contains(TASTE) && isOtherVersion) {
            props.setGastronomy(body);
        }
        if (title.contains(FLAVOR)) {
            props.setFlavor(body);
        }
        if (title.contains(GASTRONOMY)) {
            props.setGastronomy(body);
        }
    }

    /**
     * Получение списка вин в "сыром" виде() с заданной страницы.
     *
     * @param page Номер страницы, с которой мы парсим и получаем вина.
     * @return Список вин.
     */
    private List<AmWine> getAmWines(Long page) {
        long fetchStart = System.nanoTime();
        final Document document = client.getPage(page);
        long fetchEnd = System.nanoTime();
        metricsCollector.timeWinePageFetchingDuration(fetchEnd - fetchStart);
        return getAmWines(document);
    }

    @Override
    public List<AmWine> getAmWines(Document document) {
        if (document == null) {
            return Collections.emptyList();
        }
        long parseStart = System.nanoTime();
        String rawWines = getRawValue(document, PROD_NAME, PROD_PATTERN);
        try {
            List<AmWine> res = rawWines != null ?
                    mapper.readValue(rawWines, new TypeReference<>() {
                    }) :
                    Collections.emptyList();
            long parseEnd = System.nanoTime();
            metricsCollector.timeWinePageParsingDuration(parseEnd - parseStart);
            return res;
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
        return getCatalogPagesAmount(client.getMainPage());
    }

    public Long getCatalogPagesAmount(Document document) {
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

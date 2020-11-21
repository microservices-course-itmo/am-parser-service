package com.wine.to.up.am.parser.service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.to.up.am.parser.service.model.dto.AmWine;
import com.wine.to.up.am.parser.service.model.dto.Dictionary;
import com.wine.to.up.am.parser.service.service.AmClient;
import com.wine.to.up.am.parser.service.service.AmService;
import com.wine.to.up.am.parser.service.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Service
@Slf4j
public class AmServiceImpl implements AmService {

    private final AmClient client;

    private final ProxyService proxyService;

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

    public AmServiceImpl(AmClient client, ProxyService proxyService) {
        this.client = client;
        this.proxyService = proxyService;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * {@inheritDoc}
     */
    public void parseAmWines(OnPageParseCallback callback) {
        Long pages = getCatalogPagesAmount();
        AtomicLong page = new AtomicLong(1);

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        Callable<String> callableTask = () -> {
            while (page.longValue() <= pages) {
                Long currentPage = page.getAndIncrement();
                Proxy proxy = proxyService.nextProxy();
                List<AmWine> wines = parseAmWines(currentPage, proxy);
                if (wines != null && !wines.isEmpty()) {
                    callback.handlePage(wines);
                }
            }
            return "Task exec";
        };
        List<Callable<String>> callableTasks = Collections.nCopies(20, callableTask);

        try {
            List<Future<String>> futures = executorService.invokeAll(callableTasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public interface OnPageParseCallback {

        void handlePage(List<AmWine> amWines);
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
    private List<AmWine> parseAmWines(Long page, Proxy proxy) {
        final Document document = client.getPage(page, proxy);
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

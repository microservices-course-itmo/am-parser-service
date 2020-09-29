package com.wine.to.up.am.parser.service.parse.client;

import com.wine.to.up.am.parser.service.configuration.ParserConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Тест-класс для класса Client
 *
 * @author Vladimir Alexeev
 * @since 29.09.2020
 */

@Slf4j
@SpringBootTest
public class ClientTest {
    private Client client;

    @Before
    public void init() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ParserConfiguration.class);
        client = (Client) context.getBean("client");
    }

    @Test
    public void statusRequest() throws IOException {
        Connection con = Jsoup
                .connect(client.getBaseUrl() + "/kindzmarauli-shumi/")
                .userAgent(client.getUserAgent());
        Connection.Response res = con.execute();
        assertEquals(200, res.statusCode());
    }

    @Test
    public void emptyDocument() throws IOException {
        Document document = Jsoup
                .connect(client.getBaseUrl() + "/kindzmarauli-shumi/")
                .userAgent(client.getUserAgent()).get();
        assertNotNull(document);
    }
}
package com.wine.to.up.am.parser.service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.to.up.am.parser.service.model.dto.WineDto;
import com.wine.to.up.am.parser.service.service.RestService;
import com.wine.to.up.am.parser.service.service.SearchService;
import com.wine.to.up.am.parser.service.service.UpdateService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author : SSyrova
 * @since : 08.10.2020, чт
 **/
@Service
public class RestServiceImpl implements RestService {

    @Resource
    private SearchService searchService;

    @Resource
    private UpdateService updateService;

    @Override
    public List<WineDto> findAllLessByRub(Double price) {
        return searchService.findAllLessByRub(price);
    }

    @Override
    public void updateDictionary() {
        updateService.updateDictionary();
    }

    @Override
    public void updateWines() {
        updateService.updateWines();
    }

    @Override
    public void updateAll() {
        updateService.updateDictionary();
        updateService.updateWines();
    }

    @Override
    public void readAsFile(HttpServletResponse httpServletResponse) throws IOException {
        List<WineDto> wines = searchService.findAll();
        httpServletResponse.setContentType("application/json");
        PrintWriter out = httpServletResponse.getWriter();
        out.print(new ObjectMapper().writeValueAsString(wines));
        out.flush();
    }
}

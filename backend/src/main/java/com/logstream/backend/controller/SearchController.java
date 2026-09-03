package com.logstream.backend.controller;

import com.logstream.backend.dto.LogResponse;
import com.logstream.backend.service.SearchService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(
            SearchService searchService) {

        this.searchService = searchService;
    }

    @GetMapping
    public List<LogResponse> search(

            @RequestParam(
                    required = false,
                    name = "q"
            )
            String keyword,

            @RequestParam(
                    required = false
            )
            String service,

            @RequestParam(
                    required = false
            )
            String level) {

        return searchService.search(
                keyword,
                service,
                level
        );
    }
}
package com.logstream.backend.controller;

import com.logstream.backend.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(
            AnalyticsService analyticsService) {

        this.analyticsService = analyticsService;
    }

    @GetMapping("/levels")
    public List<Map<String, Object>> getLogLevelAnalytics() {
        return analyticsService.getLogLevelAnalytics();
    }

    @GetMapping("/services")
    public List<Map<String, Object>> getServiceAnalytics() {
        return analyticsService.getServiceAnalytics();
    }
}

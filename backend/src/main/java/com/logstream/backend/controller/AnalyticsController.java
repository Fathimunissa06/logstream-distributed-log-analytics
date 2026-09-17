package com.logstream.backend.controller;

import com.logstream.backend.service.AggregationService;
import com.logstream.backend.service.AnalyticsService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AggregationService aggregationService;

    public AnalyticsController(
            AnalyticsService analyticsService,
            AggregationService aggregationService) {

        this.analyticsService = analyticsService;
        this.aggregationService = aggregationService;
    }

    /**
     * Existing frontend analytics endpoint.
     */
    @GetMapping("/levels")
    public List<Map<String, Object>> getLogLevelAnalytics() {
        return analyticsService.getLogLevelAnalytics();
    }

    /**
     * Existing frontend analytics endpoint.
     */
    @GetMapping("/services")
    public List<Map<String, Object>> getServiceAnalytics() {
        return analyticsService.getServiceAnalytics();
    }

    /**
     * Upstream analytics endpoint.
     */
    @GetMapping("/volume")
    public Map<String, Long> volume(
            @RequestParam(defaultValue = "30") int minutes) {

        return aggregationService.getLogVolumePerMinute(minutes);
    }
}
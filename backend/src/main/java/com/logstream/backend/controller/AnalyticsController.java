package com.logstream.backend.controller;

import com.logstream.backend.service.AggregationService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Week 3: Analytics & Search UI (backend half).
 *
 * Two simple GET endpoints the React dashboard calls to draw its charts.
 * Both just delegate to AggregationService and return a plain JSON map -
 * Spring automatically turns a Java Map into JSON, so no extra DTO
 * classes are needed here.
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AggregationService aggregationService;

    public AnalyticsController(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    /**
     * GET /api/analytics/volume?minutes=30
     * -> { "14:28": 12, "14:29": 40, ... }
     */
    @GetMapping("/volume")
    public Map<String, Long> volume(
            @RequestParam(defaultValue = "30") int minutes) {

        return aggregationService.getLogVolumePerMinute(minutes);
    }

    /**
     * GET /api/analytics/levels?minutes=30
     * -> { "ERROR": 342, "WARN": 128, "INFO": 900 }
     */
    @GetMapping("/levels")
    public Map<String, Long> levels(
            @RequestParam(defaultValue = "30") int minutes) {

        return aggregationService.getLogCountsByLevel(minutes);
    }
}

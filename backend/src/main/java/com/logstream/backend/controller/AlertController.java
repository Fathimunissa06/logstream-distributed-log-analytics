package com.logstream.backend.controller;

import com.logstream.backend.model.AlertEvent;
import com.logstream.backend.model.AlertRule;
import com.logstream.backend.service.AlertService;

import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Week 4: REST API the React Alerts page talks to.
 *
 *   GET    /api/alerts          -> list all saved rules
 *   POST   /api/alerts          -> create a new rule
 *   DELETE /api/alerts/{id}     -> delete a rule
 *   GET    /api/alerts/history  -> list of times an alert fired
 */
@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public Collection<AlertRule> listRules() {
        return alertService.listRules();
    }

    @PostMapping
    public AlertRule createRule(@RequestBody AlertRule rule) {
        return alertService.createRule(rule);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteRule(@PathVariable String id) {
        boolean deleted = alertService.deleteRule(id);
        return Map.of("deleted", deleted);
    }

    @GetMapping("/history")
    public List<AlertEvent> history() {
        return alertService.getHistory();
    }
}

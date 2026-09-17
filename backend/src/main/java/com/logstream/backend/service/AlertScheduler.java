package com.logstream.backend.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AlertScheduler {

    private final AlertService alertService;

    public AlertScheduler(AlertService alertService) {
        this.alertService = alertService;
    }

    /**
     * Evaluate saved alert rules every minute.
     */
    @Scheduled(fixedRate = 60000)
    public void evaluateAlerts() {

        System.out.println(
                "[AlertEngine] Evaluating alert rules..."
        );

        alertService.evaluateAlerts();
    }
}
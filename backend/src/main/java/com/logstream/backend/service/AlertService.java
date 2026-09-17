package com.logstream.backend.service;

import com.logstream.backend.model.Alert;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AlertService {

    private final List<Alert> alerts = new ArrayList<>();

    private final LuceneService luceneService;

    public AlertService(LuceneService luceneService) {
        this.luceneService = luceneService;
    }

    public synchronized List<Alert> getAllAlerts() {
        return new ArrayList<>(alerts);
    }

    public synchronized Alert createAlert(Alert alert) {

        alert.setId(UUID.randomUUID().toString());

        if (alert.getService() == null ||
                alert.getService().isBlank()) {

            alert.setService("ALL");
        }

        if (alert.getWindowMinutes() <= 0) {
            alert.setWindowMinutes(5);
        }

        alert.setTriggered(false);
        alert.setLastTriggered(null);

        alerts.add(0, alert);

        return alert;
    }

    public synchronized boolean deleteAlert(String id) {

        return alerts.removeIf(
                alert -> alert.getId().equals(id)
        );
    }

    public synchronized Alert updateAlert(
            String id,
            Alert updatedAlert) {

        for (Alert alert : alerts) {

            if (alert.getId().equals(id)) {

                alert.setName(
                        updatedAlert.getName()
                );

                alert.setService(
                        updatedAlert.getService()
                );

                alert.setCondition(
                        updatedAlert.getCondition()
                );

                alert.setThreshold(
                        updatedAlert.getThreshold()
                );

                alert.setWindowMinutes(
                        updatedAlert.getWindowMinutes()
                );

                alert.setSeverity(
                        updatedAlert.getSeverity()
                );

                alert.setEnabled(
                        updatedAlert.isEnabled()
                );

                return alert;
            }
        }

        return null;
    }

    /**
     * Evaluate all enabled alert rules.
     */
    public synchronized void evaluateAlerts() {

        for (Alert alert : alerts) {

            if (!alert.isEnabled()) {
                continue;
            }

            evaluateAlert(alert);
        }
    }

    /**
     * Evaluate one alert using the exact Lucene
     * document count within the configured time window.
     */
    private void evaluateAlert(Alert alert) {

        int windowMinutes =
                alert.getWindowMinutes();

        Instant now =
                Instant.now();

        Instant cutoff =
                now.minus(
                        Duration.ofMinutes(
                                windowMinutes
                        )
                );

        /*
         * "ALL" means no service filter.
         */
        String service =
                "ALL".equalsIgnoreCase(
                        alert.getService()
                )
                        ? null
                        : alert.getService();

        /*
         * Convert alert condition into
         * the corresponding log level.
         */
        String level =
                conditionLevel(alert);

        /*
         * IMPORTANT:
         *
         * Do not use searchLogs() here because
         * searchLogs() returns only the top 100 results.
         *
         * countLogs() counts ALL matching Lucene
         * documents inside the requested time window.
         */
        int matchingLogs =
                luceneService.countLogs(
                        service,
                        level,
                        cutoff,
                        now
                );

        System.out.println(
                "[AlertEngine] " +
                alert.getName() +
                " -> matching logs: " +
                matchingLogs +
                ", threshold: " +
                alert.getThreshold()
        );

        /*
         * Alert is breached when matching logs
         * are greater than the configured threshold.
         */
        boolean breached =
                matchingLogs > alert.getThreshold();

        /*
         * Trigger webhook only when the alert
         * changes from non-triggered to triggered.
         */
        if (breached && !alert.isTriggered()) {

            alert.setTriggered(true);

            alert.setLastTriggered(
                    now.toString()
            );

            triggerSimulatedWebhook(
                    alert,
                    matchingLogs
            );

        } else if (!breached) {

            /*
             * Reset the triggered state once the
             * condition is no longer breached.
             */
            alert.setTriggered(false);
        }
    }

    /**
     * Convert an alert condition into a
     * Lucene log-level filter.
     */
    private String conditionLevel(
            Alert alert) {

        if ("Error rate".equalsIgnoreCase(
                alert.getCondition())) {

            return "ERROR";
        }

        return null;
    }

    /**
     * Simulated webhook required by the
     * project specification.
     */
    private void triggerSimulatedWebhook(
            Alert alert,
            long matchingLogs) {

        System.out.println(
                "\n========== ALERT WEBHOOK =========="
        );

        System.out.println(
                "Alert: " + alert.getName()
        );

        System.out.println(
                "Service: " + alert.getService()
        );

        System.out.println(
                "Condition: " + alert.getCondition()
        );

        System.out.println(
                "Threshold: " + alert.getThreshold()
        );

        System.out.println(
                "Window: " + alert.getWindowMinutes()
                        + " minutes"
        );

        System.out.println(
                "Matching logs: " + matchingLogs
        );

        System.out.println(
                "Severity: " + alert.getSeverity()
        );

        System.out.println(
                "Webhook status: TRIGGERED"
        );

        System.out.println(
                "===================================\n"
        );
    }
}
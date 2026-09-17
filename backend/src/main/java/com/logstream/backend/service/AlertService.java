package com.logstream.backend.service;

import com.logstream.backend.model.AlertEvent;
import com.logstream.backend.model.AlertRule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Week 4: Alerting Engine.
 *
 * WHAT THIS CLASS DOES (in plain English):
 *   1. Lets the user save/list/delete "rules" like
 *      "alert me if ERROR count from billing-api >= 5 in 5 minutes".
 *   2. Every few seconds, automatically re-checks EVERY saved rule
 *      against the Lucene index using LuceneService.countLogs().
 *   3. If a rule's count is now >= its threshold, it "fires":
 *      - a record is added to the in-memory history list
 *      - a webhook is POSTed if a URL was given (otherwise it just
 *        prints a "SIMULATED WEBHOOK" line to the console)
 *
 * STORAGE: rules and history live in memory (a ConcurrentHashMap /
 * LinkedList) rather than a real database. That is completely fine for
 * a learning project - it keeps the focus on the scheduling + querying
 * logic instead of database setup. Restarting the backend clears them.
 */
@Service
public class AlertService {

    private final LuceneService luceneService;

    // Thread-safe map of ruleId -> rule. ConcurrentHashMap is used
    // (instead of a plain HashMap) because the @Scheduled method below
    // runs on a background thread while REST requests can arrive on a
    // different thread at the same time - this map is safe for both to
    // touch at once.
    private final Map<String, AlertRule> rules = new ConcurrentHashMap<>();

    // Most-recent-first list of "an alert fired" events, capped so it
    // can't grow forever.
    private final LinkedList<AlertEvent> history = new LinkedList<>();
    private static final int MAX_HISTORY = 200;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public AlertService(LuceneService luceneService) {
        this.luceneService = luceneService;
    }

    // ---------- CRUD used by AlertController ----------

    public Collection<AlertRule> listRules() {
        return rules.values();
    }

    public AlertRule createRule(AlertRule incoming) {

        incoming.setId(UUID.randomUUID().toString());

        if (incoming.getWindowMinutes() <= 0) {
            incoming.setWindowMinutes(5); // sensible default
        }

        rules.put(incoming.getId(), incoming);

        return incoming;
    }

    public boolean deleteRule(String id) {
        return rules.remove(id) != null;
    }

    public List<AlertEvent> getHistory() {
        synchronized (history) {
            return new ArrayList<>(history);
        }
    }

    // ---------- The actual scheduled engine ----------

    /**
     * Runs automatically every 15 seconds (fixedRate = milliseconds
     * between runs). Spring Boot handles calling this for us - the
     * only reason it works is the @EnableScheduling annotation on
     * BackendApplication.java plus this @Scheduled annotation here.
     *
     * In a real production system this would run every minute per the
     * project plan; 15s is used here so a beginner testing locally
     * doesn't have to wait long to see an alert fire.
     */
    @Scheduled(fixedRate = 15_000)
    public void evaluateRules() {

        for (AlertRule rule : rules.values()) {

            if (!rule.isEnabled()) {
                continue;
            }

            try {
                evaluateSingleRule(rule);
            } catch (Exception e) {
                // One broken rule should never stop the others from
                // being checked.
                System.err.println(
                        "[AlertService] Failed to evaluate rule "
                                + rule.getName() + ": " + e.getMessage()
                );
            }
        }
    }

    private void evaluateSingleRule(AlertRule rule) throws IOException {

        long toMillis = System.currentTimeMillis();
        long fromMillis = toMillis - (rule.getWindowMinutes() * 60_000L);

        long matchedCount = luceneService.countLogs(
                rule.getKeyword(),
                rule.getService(),
                rule.getLevel(),
                fromMillis,
                toMillis
        );

        rule.setLastCheckedAt(Instant.now());

        if (matchedCount >= rule.getThreshold()) {
            fire(rule, matchedCount);
        }
    }

    private void fire(AlertRule rule, long matchedCount) {

        Instant now = Instant.now();

        rule.setLastTriggeredAt(now);
        rule.setLastTriggerCount(matchedCount);

        boolean webhookSent = sendWebhook(rule, matchedCount);

        AlertEvent event = new AlertEvent(
                rule.getId(),
                rule.getName(),
                matchedCount,
                rule.getThreshold(),
                now,
                webhookSent,
                rule.getWebhookUrl()
        );

        synchronized (history) {
            history.addFirst(event);
            while (history.size() > MAX_HISTORY) {
                history.removeLast();
            }
        }
    }

    /**
     * Sends (or "simulates") the webhook call.
     *
     * If the rule has a real URL, we actually POST a small JSON body
     * to it using Java's built-in HttpClient (no extra library
     * needed). Try https://webhook.site to get a free throwaway URL
     * to test this against.
     *
     * If no URL was given, we just print the payload to the console -
     * this is the "simulated webhook" the project plan asks for, so
     * you can see the alerting logic works even without a real
     * endpoint to call.
     */
    private boolean sendWebhook(AlertRule rule, long matchedCount) {

        String payload = String.format(
                "{\"rule\":\"%s\",\"matchedCount\":%d,\"threshold\":%d,\"windowMinutes\":%d}",
                rule.getName(), matchedCount, rule.getThreshold(), rule.getWindowMinutes()
        );

        if (rule.getWebhookUrl() == null || rule.getWebhookUrl().isBlank()) {

            System.out.println(
                    "[SIMULATED WEBHOOK] Rule '" + rule.getName()
                            + "' fired -> " + payload
            );

            return false;
        }

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(rule.getWebhookUrl()))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<Void> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.discarding()
            );

            System.out.println(
                    "[WEBHOOK] Rule '" + rule.getName() + "' -> "
                            + rule.getWebhookUrl()
                            + " responded " + response.statusCode()
            );

            return true;

        } catch (Exception e) {

            System.err.println(
                    "[WEBHOOK] Failed to call " + rule.getWebhookUrl()
                            + ": " + e.getMessage()
            );

            return false;
        }
    }
}

package com.logstream.backend.model;

import java.time.Instant;

/**
 * Week 4: Alerting Engine.
 *
 * A plain data holder describing ONE saved rule, e.g.
 * "if there are more than 5 ERROR logs from billing-api
 *  in the last 5 minutes, fire a webhook".
 *
 * This is intentionally a simple class (just fields + getters/setters)
 * so a beginner can read every field and immediately know what it
 * means - no hidden magic.
 */
public class AlertRule {

    private String id;
    private String name;

    // Optional filters - any of these can be left blank to mean "any".
    private String keyword;
    private String service;
    private String level;

    // "Fire if count >= threshold within the last windowMinutes minutes".
    private int threshold;
    private int windowMinutes;

    // Where to POST the alert when it fires. Can be left blank -
    // if blank, the alert is just printed to the console ("simulated").
    private String webhookUrl;

    private boolean enabled = true;

    // Bookkeeping so the UI/history can show what happened.
    private Instant lastCheckedAt;
    private Instant lastTriggeredAt;
    private long lastTriggerCount;

    public AlertRule() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getWindowMinutes() {
        return windowMinutes;
    }

    public void setWindowMinutes(int windowMinutes) {
        this.windowMinutes = windowMinutes;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Instant getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(Instant lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public Instant getLastTriggeredAt() {
        return lastTriggeredAt;
    }

    public void setLastTriggeredAt(Instant lastTriggeredAt) {
        this.lastTriggeredAt = lastTriggeredAt;
    }

    public long getLastTriggerCount() {
        return lastTriggerCount;
    }

    public void setLastTriggerCount(long lastTriggerCount) {
        this.lastTriggerCount = lastTriggerCount;
    }
}

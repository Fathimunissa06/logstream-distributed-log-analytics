package com.logstream.backend.model;

import java.time.Instant;

/**
 * Week 4: one row in the "alert history" list - a record that a
 * specific rule fired at a specific time with a specific count.
 * The frontend's Alerts page lists these so the user can see what
 * actually happened, not just what rules exist.
 */
public class AlertEvent {

    private String ruleId;
    private String ruleName;
    private long matchedCount;
    private int threshold;
    private Instant triggeredAt;
    private boolean webhookSent;
    private String webhookUrl;

    public AlertEvent() {
    }

    public AlertEvent(
            String ruleId,
            String ruleName,
            long matchedCount,
            int threshold,
            Instant triggeredAt,
            boolean webhookSent,
            String webhookUrl) {

        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.matchedCount = matchedCount;
        this.threshold = threshold;
        this.triggeredAt = triggeredAt;
        this.webhookSent = webhookSent;
        this.webhookUrl = webhookUrl;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public long getMatchedCount() {
        return matchedCount;
    }

    public void setMatchedCount(long matchedCount) {
        this.matchedCount = matchedCount;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public Instant getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(Instant triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public boolean isWebhookSent() {
        return webhookSent;
    }

    public void setWebhookSent(boolean webhookSent) {
        this.webhookSent = webhookSent;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
}

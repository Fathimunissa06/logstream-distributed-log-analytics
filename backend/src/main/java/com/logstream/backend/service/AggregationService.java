package com.logstream.backend.service;

import com.logstream.backend.model.LogRecord;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Week 3: Aggregations.
 *
 * This service does NOT talk to Lucene directly for writing - it only
 * reads back logs that are already indexed and groups ("aggregates")
 * them into buckets that are easy for a chart to draw:
 *
 *   - getLogVolumePerMinute(): "how many logs arrived each minute?"
 *     -> powers the line chart / histogram on the dashboard.
 *
 *   - getLogCountsByLevel(): "how many ERROR vs WARN vs INFO logs?"
 *     -> powers a simple breakdown chart / stat cards.
 */
@Service
public class AggregationService {

    private final LuceneService luceneService;

    /*
     * Formats a timestamp down to just "HH:mm" (hours:minutes),
     * e.g. "14:32". Two logs that both happened at 14:32 will land
     * in the same bucket even if their seconds differ.
     */
    private static final DateTimeFormatter MINUTE_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm")
                    .withZone(ZoneOffset.UTC);

    public AggregationService(LuceneService luceneService) {
        this.luceneService = luceneService;
    }

    /**
     * Returns a map like:
     *   { "14:28": 12, "14:29": 40, "14:30": 35, ... }
     * covering the last `lastMinutes` minutes, including minutes
     * with zero logs (so the chart doesn't have confusing gaps).
     */
    public Map<String, Long> getLogVolumePerMinute(int lastMinutes) {

        long toMillis = System.currentTimeMillis();
        long fromMillis = toMillis - (lastMinutes * 60_000L);

        Map<String, Long> buckets = new LinkedHashMap<>();

        /*
         * Step 1: pre-fill every minute in the window with 0.
         * LinkedHashMap keeps insertion order, so the chart's
         * x-axis will already be in correct time order.
         */
        for (int i = lastMinutes - 1; i >= 0; i--) {

            String label =
                    MINUTE_FORMAT.format(
                            Instant.ofEpochMilli(toMillis - (i * 60_000L))
                    );

            buckets.put(label, 0L);
        }

        try {

            /*
             * Step 2: pull back every log in this time window
             * (capped at 50,000 so a huge index can't overload
             * the server memory on a single chart request).
             */
            List<LogRecord> logs =
                    luceneService.searchByTimeRange(
                            fromMillis,
                            toMillis,
                            50_000
                    );

            /*
             * Step 3: drop each log into its minute bucket.
             */
            for (LogRecord log : logs) {

                try {

                    Instant instant =
                            Instant.parse(log.getTimestamp());

                    String label =
                            MINUTE_FORMAT.format(instant);

                    buckets.merge(label, 1L, Long::sum);

                } catch (Exception ignored) {
                    // Skip any log with a missing/unparseable timestamp.
                }
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to compute log volume",
                    e
            );
        }

        return buckets;
    }

    /**
     * Returns a map like:
     *   { "ERROR": 342, "WARN": 128, "INFO": 900 }
     * for the last `lastMinutes` minutes.
     */
    public Map<String, Long> getLogCountsByLevel(int lastMinutes) {

        long toMillis = System.currentTimeMillis();
        long fromMillis = toMillis - (lastMinutes * 60_000L);

        Map<String, Long> counts = new LinkedHashMap<>();

        try {

            List<LogRecord> logs =
                    luceneService.searchByTimeRange(
                            fromMillis,
                            toMillis,
                            50_000
                    );

            for (LogRecord log : logs) {

                String level =
                        (log.getLevel() == null || log.getLevel().isBlank())
                                ? "UNKNOWN"
                                : log.getLevel();

                counts.merge(level, 1L, Long::sum);
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to compute level counts",
                    e
            );
        }

        return counts;
    }
}

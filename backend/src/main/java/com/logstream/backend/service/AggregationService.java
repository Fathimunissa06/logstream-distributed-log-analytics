package com.logstream.backend.service;

import com.logstream.backend.model.LogRecord;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Week 3: Aggregations.
 *
 * This service reads logs that are already indexed in Lucene
 * and groups them into buckets that are easy for the frontend
 * dashboard to display.
 */
@Service
public class AggregationService {

    private final LuceneService luceneService;

    /*
     * Formats timestamps down to HH:mm.
     */
    private static final DateTimeFormatter MINUTE_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm")
                    .withZone(ZoneOffset.UTC);

    public AggregationService(LuceneService luceneService) {
        this.luceneService = luceneService;
    }

    /**
     * Returns log volume for each minute in the requested window.
     *
     * Example:
     * { "14:28": 12, "14:29": 40, "14:30": 35 }
     */
    public Map<String, Long> getLogVolumePerMinute(
            int lastMinutes) {

        long toMillis =
                System.currentTimeMillis();

        long fromMillis =
                toMillis -
                        (lastMinutes * 60_000L);

        Map<String, Long> buckets =
                new LinkedHashMap<>();

        /*
         * Pre-fill every minute with zero.
         *
         * This keeps the chart continuous even when
         * there are no logs during a particular minute.
         */
        for (int i = lastMinutes - 1;
             i >= 0;
             i--) {

            String label =
                    MINUTE_FORMAT.format(
                            Instant.ofEpochMilli(
                                    toMillis -
                                            (i * 60_000L)
                            )
                    );

            buckets.put(label, 0L);
        }

        try {

            /*
             * Pull logs from the requested time window.
             *
             * The 50,000 limit prevents an extremely large
             * result from consuming excessive memory.
             */
            List<LogRecord> logs =
                    luceneService.searchByTimeRange(
                            fromMillis,
                            toMillis,
                            50_000
                    );

            /*
             * Put every log into its corresponding
             * minute bucket.
             */
            for (LogRecord log : logs) {

                try {

                    Instant instant =
                            Instant.parse(
                                    log.getTimestamp()
                            );

                    String label =
                            MINUTE_FORMAT.format(
                                    instant
                            );

                    buckets.merge(
                            label,
                            1L,
                            Long::sum
                    );

                } catch (Exception ignored) {

                    /*
                     * Ignore logs with missing or
                     * invalid timestamps.
                     */
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to compute log volume",
                    e
            );
        }

        return buckets;
    }

    /**
     * Returns the number of logs grouped by level
     * for the requested time window.
     *
     * Example:
     * { "ERROR": 342, "WARN": 128, "INFO": 900 }
     */
    public Map<String, Long> getLogCountsByLevel(
            int lastMinutes) {

        long toMillis =
                System.currentTimeMillis();

        long fromMillis =
                toMillis -
                        (lastMinutes * 60_000L);

        Map<String, Long> counts =
                new LinkedHashMap<>();

        try {

            List<LogRecord> logs =
                    luceneService.searchByTimeRange(
                            fromMillis,
                            toMillis,
                            50_000
                    );

            for (LogRecord log : logs) {

                String level =
                        (log.getLevel() == null ||
                                log.getLevel().isBlank())
                                ? "UNKNOWN"
                                : log.getLevel();

                counts.merge(
                        level,
                        1L,
                        Long::sum
                );
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to compute level counts",
                    e
            );
        }

        return counts;
    }
}
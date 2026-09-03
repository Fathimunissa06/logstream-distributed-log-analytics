package com.logstream.backend.service;

import com.logstream.backend.dto.LogResponse;
import com.logstream.backend.model.LogRecord;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final LuceneService luceneService;

    public SearchService(LuceneService luceneService) {
        this.luceneService = luceneService;
    }

    public List<LogResponse> search(
            String keyword,
            String service,
            String level) {

        List<LogRecord> logs =
                luceneService.searchLogs(
                        keyword,
                        service,
                        level
                );

        return logs.stream()
                .map(log ->
                        new LogResponse(
                                log.getTimestamp(),
                                log.getService(),
                                log.getLevel(),
                                log.getMessage()
                        )
                )
                .toList();
    }
}
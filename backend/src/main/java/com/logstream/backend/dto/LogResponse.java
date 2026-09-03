package com.logstream.backend.dto;

public class LogResponse {

    private String timestamp;
    private String service;
    private String level;
    private String message;

    public LogResponse() {
    }

    public LogResponse(
            String timestamp,
            String service,
            String level,
            String message) {

        this.timestamp = timestamp;
        this.service = service;
        this.level = level;
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getService() {
        return service;
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }
}
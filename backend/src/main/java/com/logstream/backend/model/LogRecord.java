package com.logstream.backend.model;

public class LogRecord {

    private String timestamp;
    private String service;
    private String level;
    private String message;

    public LogRecord() {
    }

    public LogRecord(
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

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
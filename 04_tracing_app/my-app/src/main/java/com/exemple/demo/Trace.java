package com.exemple.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Classe simple pour représenter une trace
 */
public class Trace {
    private String traceId;
    private String spanId;
    private String parentSpanId;
    private String operationName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long durationMs;
    private String status;
    private String tags;

    public Trace(String operationName) {
        this.traceId = UUID.randomUUID().toString().substring(0, 8);
        this.spanId = UUID.randomUUID().toString().substring(0, 8);
        this.operationName = operationName;
        this.startTime = LocalDateTime.now();
        this.status = "STARTED";
        this.tags = "";
    }

    public Trace(String operationName, String parentTraceId, String parentSpanId) {
        this(operationName);
        this.traceId = parentTraceId;
        this.parentSpanId = parentSpanId;
    }

    public void finish() {
        this.endTime = LocalDateTime.now();
        this.durationMs = java.time.Duration.between(startTime, endTime).toMillis();
        this.status = "FINISHED";
    }

    public void addTag(String key, String value) {
        if (this.tags.isEmpty()) {
            this.tags = key + "=" + value;
        } else {
            this.tags += ", " + key + "=" + value;
        }
    }

    public void setError(String error) {
        this.status = "ERROR";
        addTag("error", error);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        return String.format("[%s] %s | %s -> %s | %s (%dms) | %s | %s",
                traceId,
                spanId,
                startTime.format(formatter),
                endTime != null ? endTime.format(formatter) : "...",
                operationName,
                durationMs,
                status,
                tags);
    }

    // Getters
    public String getTraceId() { return traceId; }
    public String getSpanId() { return spanId; }
    public String getParentSpanId() { return parentSpanId; }
    public String getOperationName() { return operationName; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public long getDurationMs() { return durationMs; }
    public String getStatus() { return status; }
    public String getTags() { return tags; }
}

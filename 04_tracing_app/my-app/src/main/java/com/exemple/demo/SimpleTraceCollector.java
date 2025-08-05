package com.exemple.demo;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Collecteur de traces simple qui stocke les traces en mémoire et les écrit dans un fichier
 */
@Service
public class SimpleTraceCollector {
    
    private final List<Trace> traces = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<String, Trace> activeTraces = new ConcurrentHashMap<>();
    private final String traceFile = "traces.log";

    public Trace startTrace(String operationName) {
        Trace trace = new Trace(operationName);
        activeTraces.put(trace.getSpanId(), trace);
        logToConsole("🟢 TRACE STARTED: " + trace.getOperationName() + " [" + trace.getTraceId() + ":" + trace.getSpanId() + "]");
        return trace;
    }

    public Trace startChildTrace(String operationName, String parentTraceId, String parentSpanId) {
        Trace trace = new Trace(operationName, parentTraceId, parentSpanId);
        activeTraces.put(trace.getSpanId(), trace);
        logToConsole("🔵 CHILD TRACE STARTED: " + trace.getOperationName() + " [" + trace.getTraceId() + ":" + trace.getSpanId() + "] parent:" + parentSpanId);
        return trace;
    }

    public void finishTrace(Trace trace) {
        trace.finish();
        activeTraces.remove(trace.getSpanId());
        traces.add(trace);
        
        String statusIcon = "✅";
        if ("ERROR".equals(trace.getStatus())) {
            statusIcon = "❌";
        }
        
        logToConsole(statusIcon + " TRACE FINISHED: " + trace.getOperationName() + 
                    " [" + trace.getTraceId() + ":" + trace.getSpanId() + "] " + 
                    trace.getDurationMs() + "ms");
        
        // Écrire dans le fichier
        writeToFile(trace);
    }

    public void addError(Trace trace, String error) {
        trace.setError(error);
        logToConsole("🚨 TRACE ERROR: " + trace.getOperationName() + " [" + trace.getTraceId() + ":" + trace.getSpanId() + "] " + error);
    }

    public List<Trace> getAllTraces() {
        return new ArrayList<>(traces);
    }

    public List<Trace> getTracesByTraceId(String traceId) {
        return traces.stream()
                .filter(trace -> trace.getTraceId().equals(traceId))
                .toList();
    }

    public int getActiveTracesCount() {
        return activeTraces.size();
    }

    public int getTotalTracesCount() {
        return traces.size();
    }

    private void logToConsole(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        System.out.println("[" + timestamp + "] " + message);
    }

    private void writeToFile(Trace trace) {
        try (FileWriter writer = new FileWriter(traceFile, true)) {
            writer.write(trace.toString() + "\n");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture de la trace: " + e.getMessage());
        }
    }

    public void clearTraces() {
        traces.clear();
        activeTraces.clear();
        logToConsole("🧹 TRACES CLEARED");
    }
}

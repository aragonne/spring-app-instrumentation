package com.exemple.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service pour faciliter l'utilisation du tracing dans l'application
 */
@Service
public class TracingService {

    @Autowired
    private SimpleTraceCollector traceCollector;

    public Trace trace(String operationName, Runnable operation) {
        Trace trace = traceCollector.startTrace(operationName);
        try {
            operation.run();
            return trace;
        } catch (Exception e) {
            traceCollector.addError(trace, e.getMessage());
            throw e;
        } finally {
            traceCollector.finishTrace(trace);
        }
    }

    public <T> T trace(String operationName, java.util.function.Supplier<T> operation) {
        Trace trace = traceCollector.startTrace(operationName);
        try {
            T result = operation.get();
            trace.addTag("result", "success");
            return result;
        } catch (Exception e) {
            traceCollector.addError(trace, e.getMessage());
            throw e;
        } finally {
            traceCollector.finishTrace(trace);
        }
    }

    public Trace startTrace(String operationName) {
        return traceCollector.startTrace(operationName);
    }

    public void finishTrace(Trace trace) {
        traceCollector.finishTrace(trace);
    }

    public void addTraceTag(Trace trace, String key, String value) {
        trace.addTag(key, value);
    }

    public void simulateWork(int minMs, int maxMs) {
        try {
            int sleepTime = minMs + (int) (Math.random() * (maxMs - minMs));
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

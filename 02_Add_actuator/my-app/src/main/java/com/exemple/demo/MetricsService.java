package com.exemple.demo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class MetricsService {

    private final Counter bonjourCounter;
    private final Timer responseTimer;
    private final MeterRegistry meterRegistry;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.bonjourCounter = Counter.builder("bonjour.requests")
                .description("Nombre de requêtes bonjour")
                .register(meterRegistry);
        
        this.responseTimer = Timer.builder("bonjour.response.time")
                .description("Temps de réponse des requêtes bonjour")
                .register(meterRegistry);
    }

    public String processBonjour() {
        bonjourCounter.increment();
        
        try {
            return responseTimer.recordCallable(() -> {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(10, 100));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "Bonjour, Spring avec métriques!";
            });
        } catch (Exception e) {
            // En cas d'erreur, retourner une réponse par défaut
            return "Bonjour, Spring avec métriques!";
        }
    }

    public void recordCustomMetric(String operation, double value) {
        meterRegistry.gauge("custom.operation.value", value);
        meterRegistry.counter("custom.operation.count", "operation", operation).increment();
    }
}

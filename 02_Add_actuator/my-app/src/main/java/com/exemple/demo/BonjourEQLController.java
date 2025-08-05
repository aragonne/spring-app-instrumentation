package com.exemple.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BonjourEQLController {

    @Autowired
    private MetricsService metricsService;

    @GetMapping("/bonjour-eql")
    public String direBonjourEQL() {
        return metricsService.processBonjour();
    }

    @PostMapping("/metrics/custom")
    public String recordCustomMetric(@RequestParam String operation, @RequestParam double value) {
        metricsService.recordCustomMetric(operation, value);
        return "Métrique personnalisée enregistrée: " + operation + " = " + value;
    }
    
    @GetMapping("/user/{id}")
    public String getUser(@PathVariable String id) {
        // Fake work simulation on database query
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(20, 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Utilisateur: " + id;
    }

    @PostMapping("/data")
    public String postData(@RequestBody String data) {
        // Fake work simulation on data processing
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(30, 150));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Données reçues: " + data.length() + " caractères";
    }

    @GetMapping("/error")
    public String simulateError() {
        // Fake error simulation
        if (ThreadLocalRandom.current().nextBoolean()) {
            throw new RuntimeException("Erreur simulée pour démonstration OpenTelemetry");
        }
        return "Pas d'erreur cette fois!";
    }
}

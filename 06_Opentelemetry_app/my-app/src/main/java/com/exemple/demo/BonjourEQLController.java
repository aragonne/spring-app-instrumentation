package com.exemple.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BonjourEQLController {
    
    private static final Logger logger = LoggerFactory.getLogger(BonjourEQLController.class);
    private static int requestCount = 0;
    private static int errorCount = 0;

    @GetMapping("/bonjour-eql")
    public String direBonjourEQL() {
        requestCount++;
        logger.info("Requête /bonjour-eql reçue - Compteur: {}", requestCount);
        
        // Simuler un traitement avec délai variable
        int delay = ThreadLocalRandom.current().nextInt(50, 200);
        logger.debug("Simulation d'un délai de traitement: {}ms", delay);
        
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            logger.error("Interruption du thread pendant le traitement", e);
            Thread.currentThread().interrupt();
        }
        
        logger.info("Traitement terminé avec succès pour /bonjour-eql");
        return "Bonjour EQL, Voici Spring avec OpenTelemetry Agent! Requête #" + requestCount;
    }

    @GetMapping("/user/{id}")
    public String getUser(@PathVariable String id) {
        requestCount++;
        logger.info("Récupération de l'utilisateur avec ID: {}", id);
        
        // Simuler un appel à une base de données
        int dbDelay = ThreadLocalRandom.current().nextInt(20, 100);
        logger.debug("Simulation d'un appel base de données - délai: {}ms", dbDelay);
        
        try {
            Thread.sleep(dbDelay);
        } catch (InterruptedException e) {
            logger.error("Erreur lors de l'accès à la base de données pour l'utilisateur {}", id, e);
            Thread.currentThread().interrupt();
        }
        
        logger.info("Utilisateur {} récupéré avec succès", id);
        return "Utilisateur: " + id + " (délai DB: " + dbDelay + "ms)";
    }

    @PostMapping("/data")
    public String postData(@RequestBody String data) {
        requestCount++;
        logger.info("Réception de données POST - taille: {} caractères", data.length());
        
        // Simuler un traitement de données
        int processingDelay = ThreadLocalRandom.current().nextInt(30, 150);
        logger.debug("Traitement des données - délai estimé: {}ms", processingDelay);
        
        try {
            Thread.sleep(processingDelay);
        } catch (InterruptedException e) {
            logger.error("Erreur lors du traitement des données", e);
            Thread.currentThread().interrupt();
        }
        
        logger.info("Données traitées avec succès - {} caractères en {}ms", data.length(), processingDelay);
        return "Données reçues et traitées: " + data.length() + " caractères (" + processingDelay + "ms)";
    }

    @GetMapping("/error")
    public String simulateError() {
        requestCount++;
        logger.info("Endpoint /error appelé - simulation d'erreur aléatoire");
        
        // Simuler une erreur pour démontrer le tracing des erreurs
        if (ThreadLocalRandom.current().nextBoolean()) {
            errorCount++;
            logger.error("Erreur simulée #{} pour démonstration OpenTelemetry", errorCount);
            throw new RuntimeException("Erreur simulée #" + errorCount + " pour démonstration OpenTelemetry");
        }
        
        logger.info("Aucune erreur générée cette fois - requête traitée avec succès");
        return "Pas d'erreur cette fois! (Erreurs totales: " + errorCount + ")";
    }
    
    @GetMapping("/observability")
    public Map<String, Object> getObservabilityData() {
        logger.info("Endpoint /observability appelé - récupération des données d'observabilité");
        
        Map<String, Object> data = new HashMap<>();
        data.put("service_name", "spring-demo-otel");
        data.put("total_requests", requestCount);
        data.put("total_errors", errorCount);
        data.put("success_rate", requestCount > 0 ? ((double)(requestCount - errorCount) / requestCount) * 100 : 0);
        data.put("agent_info", "OpenTelemetry Java Agent - Auto-instrumentation active");
        data.put("instrumentation", Map.of(
            "traces", "Collectées automatiquement par l'agent",
            "metrics", "JVM et HTTP métriques collectées automatiquement",
            "logs", "Logs avec correlation automatique trace/span"
        ));
        
        logger.info("Données d'observabilité retournées: {} requêtes, {} erreurs", requestCount, errorCount);
        return data;
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        logger.debug("Health check appelé");
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "spring-demo-otel");
        health.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return health;
    }
}
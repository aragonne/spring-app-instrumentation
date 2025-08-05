package com.exemple.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BonjourEQLController {

    @Autowired
    private TracingService tracingService;

    @Autowired
    private SimpleTraceCollector traceCollector;

    @GetMapping("/bonjour-eql")
    public String direBonjourEQL() {
        return tracingService.trace("bonjour-eql-request", () -> {
            tracingService.simulateWork(50, 200);
            return "Bonjour EQL, Voici Spring avec tracing simple!";
        });
    }

    @GetMapping("/user/{id}")
    public String getUser(@PathVariable String id) {
        Trace mainTrace = tracingService.startTrace("get-user-request");
        try {
            mainTrace.addTag("user.id", id);
            
            // Simulate user validation
            Trace validationTrace = traceCollector.startChildTrace("validate-user", mainTrace.getTraceId(), mainTrace.getSpanId());
            tracingService.simulateWork(20, 50);
            validationTrace.addTag("validation", "success");
            traceCollector.finishTrace(validationTrace);
            
            // Fake work simulation on database query
            Trace dbTrace = traceCollector.startChildTrace("database-query", mainTrace.getTraceId(), mainTrace.getSpanId());
            tracingService.simulateWork(30, 100);
            dbTrace.addTag("query", "SELECT * FROM users WHERE id=" + id);
            traceCollector.finishTrace(dbTrace);
            
            return "Utilisateur: " + id;
        } finally {
            tracingService.finishTrace(mainTrace);
        }
    }

    @PostMapping("/data")
    public String postData(@RequestBody String data) {
      // Fake work simulation on data processing
        return tracingService.trace("process-data", () -> {
            Trace currentTrace = tracingService.startTrace("data-processing");
            try {
                currentTrace.addTag("data.size", String.valueOf(data.length()));
                tracingService.simulateWork(30, 150);
                return "Données traitées: " + data.length() + " caractères";
            } finally {
                tracingService.finishTrace(currentTrace);
            }
        });
    }

    @GetMapping("/error")
    public String simulateError() {
      // Fake error simulation
        Trace trace = tracingService.startTrace("error-simulation");
        try {
            tracingService.simulateWork(10, 50);
            if (Math.random() > 0.5) {
                throw new RuntimeException("Erreur simulée pour démonstration du tracing");
            }
            return "Pas d'erreur cette fois!";
        } catch (Exception e) {
            traceCollector.addError(trace, e.getMessage());
            throw e;
        } finally {
            tracingService.finishTrace(trace);
        }
    }
}
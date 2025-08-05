package com.exemple.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Contrôleur pour visualiser les traces
 */
@Controller
public class TraceViewerController {

    @Autowired
    private SimpleTraceCollector traceCollector;

    @GetMapping("/traces")
    public String viewTraces(Model model) {
        List<Trace> traces = traceCollector.getAllTraces();
        model.addAttribute("traces", traces);
        model.addAttribute("activeCount", traceCollector.getActiveTracesCount());
        model.addAttribute("totalCount", traceCollector.getTotalTracesCount());
        return "traces";
    }

    @GetMapping("/traces/api")
    @ResponseBody
    public List<Trace> getTracesApi() {
        return traceCollector.getAllTraces();
    }

    @GetMapping("/traces/clear")
    @ResponseBody
    public String clearTraces() {
        traceCollector.clearTraces();
        return "Traces cleared!";
    }

    @GetMapping("/traces/stats")
    @ResponseBody
    public String getStats() {
        return String.format("Active: %d, Total: %d", 
                traceCollector.getActiveTracesCount(), 
                traceCollector.getTotalTracesCount());
    }
}

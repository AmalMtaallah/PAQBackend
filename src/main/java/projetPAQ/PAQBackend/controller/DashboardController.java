package projetPAQ.PAQBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetPAQ.PAQBackend.service.DashboardService;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // Pour les totaux (non supprimés et non archivés)
    @GetMapping("/entretiens/totals")
    public ResponseEntity<Map<String, Long>> getTotalEntretienStats() {
        Map<String, Long> stats = dashboardService.getTotalEntretienCounts();
        return ResponseEntity.ok(stats);
    }

    // Pour les stats par période
    @GetMapping("/entretiens")
    public ResponseEntity<Map<String, Long>> getEntretienStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        
        if (startDate == null || endDate == null) {
            endDate = new Date();
            startDate = new Date(endDate.getTime() - (30L * 24 * 60 * 60 * 1000));
        }
        
        Map<String, Long> stats = dashboardService.getEntretienCounts(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    // Pour le nombre total de collaborateurs
    @GetMapping("/collaborateurs/total")
    public ResponseEntity<Long> getTotalCollaborateurs() {
        Long count = dashboardService.getTotalCollaborateursCount();
        return ResponseEntity.ok(count);
    }
}
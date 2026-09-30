package controller;

import logic.ReportControl.Report;
import logic.ReportControl.ReportLogic;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportLogic reportLogic = new ReportLogic();

    @GetMapping("/generate")
    public ResponseEntity<Report> generateReport(
            @RequestParam String reportType,
            @RequestParam int monthNumber,
            @RequestParam int year) {
        
        try {
            LocalDate lastDayOfMonth = LocalDate.of(year, monthNumber, 1)
                    .withDayOfMonth(LocalDate.of(year, monthNumber, 1).lengthOfMonth());
            
            Report report = reportLogic.fetchReportByDate(reportType, lastDayOfMonth);
            
            if (report != null) {
                return ResponseEntity.ok(report);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/available-dates")
    public ResponseEntity<Map<String, List<Integer>>> fetchAvailableDates() {
        try {
            // Fetch available years and months from the database
            List<Integer> years = reportLogic.getAvailableReportYears();
            List<Integer> months = reportLogic.getAvailableReportMonths();
            
            // Map them into a clean JSON structure instead of a raw Object array
            Map<String, List<Integer>> datesData = new HashMap<>();
            datesData.put("years", years);
            datesData.put("months", months);
            
            return ResponseEntity.ok(datesData);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
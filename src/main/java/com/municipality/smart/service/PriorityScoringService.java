package com.municipality.smart.service;

import com.municipality.smart.entity.Complaint;
import org.springframework.stereotype.Service;

@Service
public class PriorityScoringService {

    /**
     * Calculates priority score from 0 to 100+ and returns LOW, MEDIUM, HIGH, or CRITICAL.
     */
    public String calculatePriority(Complaint complaint, int relatedComplaintsCount, boolean isHotspot) {
        int score = 0;

        // 1. AI / Assessed Severity
        String severity = complaint.getSeverity() != null ? complaint.getSeverity().toUpperCase() : "MEDIUM";
        switch (severity) {
            case "CRITICAL" -> score += 40;
            case "HIGH" -> score += 30;
            case "MEDIUM" -> score += 20;
            case "LOW" -> score += 10;
            default -> score += 20;
        }

        // 2. Number of Related Complaints (Community Impact)
        if (relatedComplaintsCount >= 4) {
            score += 25;
        } else if (relatedComplaintsCount >= 2) {
            score += 15;
        } else if (relatedComplaintsCount == 1) {
            score += 8;
        }

        // 3. Hotspot Escalation
        if (isHotspot) {
            score += 20;
        }

        // 4. Critical Category Hazards (Traffic signals, active water mains, deep potholes)
        String category = complaint.getCategory() != null ? complaint.getCategory().toLowerCase() : "";
        if (category.contains("traffic") || category.contains("water leakage") || category.contains("pothole")) {
            score += 10;
        }

        // Threshold mapping
        if (score >= 65) {
            return "CRITICAL";
        } else if (score >= 45) {
            return "HIGH";
        } else if (score >= 25) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
}

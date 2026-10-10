package com.municipality.smart.service;

import com.municipality.smart.dto.AiAnalysisResultDto;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MockAiService {

    public AiAnalysisResultDto analyze(String description, String filename) {
        String combined = ((description != null ? description : "") + " " + (filename != null ? filename : "")).toLowerCase(Locale.ROOT);

        String category;
        String department;
        String severity;
        int confidence = 92;
        String reasoning;

        // 1. Category Classification
        if (containsAny(combined, "streetlight", "street light", "lamp", "bulb", "light pole", "electric pole", "darkness", "lighting", "street light not working")) {
            category = "Streetlight";
            department = "Electrical Department";
            confidence = containsAny(combined, "streetlight", "lamp") ? 95 : 91;
            reasoning = "Visual/textual tokens identify an illumination fixture malfunction impacting nocturnal visibility and safety.";
        } else if (containsAny(combined, "pothole", "pot hole", "crater", "sinkhole", "road pit")) {
            category = "Pothole";
            department = "Roads Department";
            confidence = 94;
            reasoning = "Surface cavity detected on vehicular carriageway presenting vehicular damage and transit risk.";
        } else if (containsAny(combined, "road damage", "asphalt", "cracked road", "broken road", "divider", "curb")) {
            category = "Road Damage";
            department = "Roads Department";
            confidence = 91;
            reasoning = "Carriageway structural degradation detected requiring resurfacing or re-paving.";
        } else if (containsAny(combined, "garbage", "trash", "waste", "rubbish", "dump", "dustbin", "bin", "litter", "foul smell", "rotting")) {
            category = "Garbage";
            department = "Sanitation Department";
            confidence = 93;
            reasoning = "Solid waste accumulation detected in public thoroughfare posing environmental and health risks.";
        } else if (containsAny(combined, "water leakage", "water leak", "pipeline", "pipe burst", "pipe leak", "potable water", "main pipe")) {
            category = "Water Leakage";
            department = "Water Supply Department";
            confidence = 94;
            reasoning = "Pressurized municipal supply line rupture causing utility loss and localized street flooding.";
        } else if (containsAny(combined, "drainage", "sewage", "drain", "manhole", "gutter", "clogged", "overflowing sewage", "stagnant water")) {
            category = "Drainage";
            department = "Drainage Department";
            confidence = 92;
            reasoning = "Stormwater / wastewater canal blockage with potential backflow into public easements.";
        } else if (containsAny(combined, "traffic signal", "traffic light", "red light", "signal light", "blinker", "zebra crossing")) {
            category = "Traffic Signal";
            department = "Traffic/Transport Department";
            confidence = 96;
            reasoning = "Active intersection control mechanism outage compromising vehicular and pedestrian right-of-way.";
        } else if (containsAny(combined, "infrastructure", "footpath", "sidewalk", "bridge", "railing", "bus stop", "flyover", "bench")) {
            category = "Public Infrastructure Damage";
            department = "Public Works Department";
            confidence = 89;
            reasoning = "Physical civic asset impairment requiring civil engineering remediation.";
        } else {
            category = "Other";
            department = "General Administration";
            confidence = 82;
            reasoning = "General civic concern identified. Assigned to municipal oversight desk for triage.";
        }

        // 2. Severity Estimation
        if (containsAny(combined, "critical", "danger", "hazardous", "accident", "fatal", "deep", "crater", "live wire", "high voltage", "burst", "sinkhole", "flooding")) {
            severity = "CRITICAL";
        } else if (containsAny(combined, "high", "night", "dark", "urgent", "major", "busy", "main road", "highway", "broken", "severe", "completely")) {
            severity = "HIGH";
        } else if (containsAny(combined, "medium", "moderate", "overflowing", "smell", "bad", "leaking", "stinking")) {
            severity = "MEDIUM";
        } else {
            // Contextual defaults: Streetlight at night/broken defaults to HIGH
            if ("Streetlight".equals(category) && containsAny(combined, "not working", "off", "dark", "night", "broken")) {
                severity = "HIGH";
            } else if ("Pothole".equals(category)) {
                severity = "HIGH";
            } else {
                severity = "MEDIUM";
            }
        }

        return new AiAnalysisResultDto(category, severity, department, confidence, "RULE_ENGINE_MOCK", reasoning);
    }

    private boolean containsAny(String source, String... targets) {
        for (String target : targets) {
            if (source.contains(target)) return true;
        }
        return false;
    }
}

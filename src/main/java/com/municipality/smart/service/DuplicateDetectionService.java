package com.municipality.smart.service;

import com.municipality.smart.entity.Complaint;
import com.municipality.smart.entity.RelatedComplaint;
import com.municipality.smart.repository.ComplaintRepository;
import com.municipality.smart.repository.RelatedComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DuplicateDetectionService {

    private final ComplaintRepository complaintRepository;
    private final RelatedComplaintRepository relatedComplaintRepository;

    @Value("${municipality.similarity.threshold:0.45}")
    private double similarityThreshold;

    @Autowired
    public DuplicateDetectionService(ComplaintRepository complaintRepository,
                                     RelatedComplaintRepository relatedComplaintRepository) {
        this.complaintRepository = complaintRepository;
        this.relatedComplaintRepository = relatedComplaintRepository;
    }

    @Transactional
    public List<RelatedComplaint> detectAndLinkDuplicates(Complaint newComplaint) {
        List<Complaint> existingComplaints = complaintRepository.findAll();
        List<RelatedComplaint> matches = new ArrayList<>();

        for (Complaint existing : existingComplaints) {
            if (existing.getId() != null && existing.getId().equals(newComplaint.getId())) {
                continue;
            }

            double sim = calculateSimilarity(newComplaint, existing);
            if (sim >= similarityThreshold) {
                String reason = buildMatchReason(newComplaint, existing, sim);
                RelatedComplaint link = new RelatedComplaint(newComplaint, existing, Math.round(sim * 100.0) / 100.0, reason);
                matches.add(link);
                relatedComplaintRepository.save(link);
            }
        }

        return matches;
    }

    public double calculateSimilarity(Complaint c1, Complaint c2) {
        // 1. Category Score (0.0 to 1.0)
        double catScore = 0.0;
        if (c1.getCategory() != null && c2.getCategory() != null) {
            if (c1.getCategory().equalsIgnoreCase(c2.getCategory())) {
                catScore = 1.0;
            } else if ((c1.getCategory().contains("Road") && c2.getCategory().contains("Road")) ||
                       (c1.getCategory().contains("Drain") && c2.getCategory().contains("Water"))) {
                catScore = 0.5;
            }
        }

        // If categories are completely unrelated, lower likelihood of civic duplicate
        if (catScore == 0.0) {
            return 0.1;
        }

        // 2. Location Score (0.0 to 1.0)
        double locScore = calculateLocationScore(c1, c2);

        // 3. Description Score (0.0 to 1.0)
        double descScore = calculateTextSimilarity(c1.getDescription(), c2.getDescription());

        // Weighted Formula: 40% Category, 40% Location, 20% Text
        return (catScore * 0.40) + (locScore * 0.40) + (descScore * 0.20);
    }

    private double calculateLocationScore(Complaint c1, Complaint c2) {
        // Coordinate distance if available
        if (c1.getLatitude() != null && c1.getLongitude() != null &&
            c2.getLatitude() != null && c2.getLongitude() != null) {
            double distanceMeters = haversine(c1.getLatitude(), c1.getLongitude(), c2.getLatitude(), c2.getLongitude());
            if (distanceMeters <= 150) return 1.0;
            if (distanceMeters <= 350) return 0.85;
            if (distanceMeters <= 600) return 0.65;
            if (distanceMeters <= 1200) return 0.40;
            return 0.10;
        }

        // String location comparison
        String loc1 = normalize(c1.getLocation());
        String loc2 = normalize(c2.getLocation());

        if (loc1.isEmpty() || loc2.isEmpty()) return 0.3;
        if (loc1.equals(loc2)) return 1.0;
        if (loc1.contains(loc2) || loc2.contains(loc1)) return 0.85;

        // Common token match
        Set<String> words1 = new HashSet<>(Arrays.asList(loc1.split("\\s+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(loc2.split("\\s+")));
        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        if (!intersection.isEmpty()) {
            return (double) intersection.size() / Math.max(words1.size(), words2.size());
        }

        return 0.0;
    }

    private double calculateTextSimilarity(String t1, String t2) {
        if (t1 == null || t2 == null) return 0.0;
        Set<String> s1 = extractKeywords(t1);
        Set<String> s2 = extractKeywords(t2);

        if (s1.isEmpty() || s2.isEmpty()) return 0.0;
        Set<String> intersection = new HashSet<>(s1);
        intersection.retainAll(s2);

        Set<String> union = new HashSet<>(s1);
        union.addAll(s2);

        return (double) intersection.size() / union.size();
    }

    private Set<String> extractKeywords(String text) {
        Set<String> stopwords = Set.of("the", "is", "at", "in", "on", "and", "a", "an", "to", "of", "for", "with", "not", "this", "it", "my", "our", "near");
        Set<String> words = new HashSet<>();
        for (String w : normalize(text).split("[^a-zA-Z0-9]+")) {
            if (w.length() > 2 && !stopwords.contains(w)) {
                words.add(w);
            }
        }
        return words;
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth radius in meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private String buildMatchReason(Complaint c1, Complaint c2, double sim) {
        int pct = (int) Math.round(sim * 100);
        return String.format("Matching Category '%s' and Location '%s' (%d%% similarity)",
                c2.getCategory(), c2.getLocation(), pct);
    }
}

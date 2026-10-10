package com.municipality.smart.service;

import com.municipality.smart.entity.Complaint;
import com.municipality.smart.entity.Hotspot;
import com.municipality.smart.repository.ComplaintRepository;
import com.municipality.smart.repository.HotspotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HotspotService {

    private final ComplaintRepository complaintRepository;
    private final HotspotRepository hotspotRepository;

    @Value("${municipality.hotspot.threshold:3}")
    private int hotspotThreshold;

    @Autowired
    public HotspotService(ComplaintRepository complaintRepository, HotspotRepository hotspotRepository) {
        this.complaintRepository = complaintRepository;
        this.hotspotRepository = hotspotRepository;
    }

    @Transactional
    public Hotspot evaluateHotspotForComplaint(Complaint complaint) {
        String category = complaint.getCategory();
        String location = complaint.getLocation();

        if (category == null || location == null) return null;

        List<Complaint> relatedInLocation = complaintRepository.findByCategoryIgnoreCaseAndLocationIgnoreCase(category, location);
        int count = relatedInLocation.size();

        if (count >= hotspotThreshold) {
            Optional<Hotspot> existingOpt = hotspotRepository.findByLocationIgnoreCaseAndCategoryIgnoreCase(location, category);
            Hotspot hotspot = existingOpt.orElseGet(Hotspot::new);

            hotspot.setLocation(location);
            hotspot.setCategory(category);
            hotspot.setReportCount(count);

            // Compute aggregated severity
            boolean hasCritical = relatedInLocation.stream().anyMatch(c -> "CRITICAL".equalsIgnoreCase(c.getSeverity()));
            boolean hasHigh = relatedInLocation.stream().anyMatch(c -> "HIGH".equalsIgnoreCase(c.getSeverity()));
            if (hasCritical) {
                hotspot.setSeverity("CRITICAL");
                hotspot.setPriority("CRITICAL");
            } else if (hasHigh || count >= 5) {
                hotspot.setSeverity("HIGH");
                hotspot.setPriority("URGENT");
            } else {
                hotspot.setSeverity("MEDIUM");
                hotspot.setPriority("HIGH");
            }

            hotspot.setStatus("ACTIVE");
            if (complaint.getDepartment() != null) {
                hotspot.setResponsibleDepartment(complaint.getDepartment().getName());
            } else {
                hotspot.setResponsibleDepartment(determineDepartment(category));
            }

            // Average coordinates if available
            double latSum = 0;
            double lonSum = 0;
            int coordCount = 0;
            for (Complaint c : relatedInLocation) {
                if (c.getLatitude() != null && c.getLongitude() != null) {
                    latSum += c.getLatitude();
                    lonSum += c.getLongitude();
                    coordCount++;
                }
            }
            if (coordCount > 0) {
                hotspot.setLatitude(latSum / coordCount);
                hotspot.setLongitude(lonSum / coordCount);
            } else if (complaint.getLatitude() != null) {
                hotspot.setLatitude(complaint.getLatitude());
                hotspot.setLongitude(complaint.getLongitude());
            }

            hotspot.setUpdatedAt(LocalDateTime.now());
            Hotspot savedHotspot = hotspotRepository.save(hotspot);

            // Update all related complaints to flag them as part of a hotspot
            for (Complaint c : relatedInLocation) {
                if (!Boolean.TRUE.equals(c.getIsHotspot())) {
                    c.setIsHotspot(true);
                    complaintRepository.save(c);
                }
            }

            return savedHotspot;
        }

        return null;
    }

    public List<Hotspot> getAllHotspots() {
        return hotspotRepository.findAllByOrderByReportCountDesc();
    }

    public Optional<Hotspot> getHotspotForLocationAndCategory(String location, String category) {
        return hotspotRepository.findByLocationIgnoreCaseAndCategoryIgnoreCase(location, category);
    }

    private String determineDepartment(String category) {
        if (category == null) return "General Administration";
        return switch (category.toLowerCase()) {
            case "streetlight" -> "Electrical Department";
            case "pothole", "road damage" -> "Roads Department";
            case "garbage" -> "Sanitation Department";
            case "water leakage" -> "Water Supply Department";
            case "drainage" -> "Drainage Department";
            case "traffic signal" -> "Traffic/Transport Department";
            case "public infrastructure damage" -> "Public Works Department";
            default -> "General Administration";
        };
    }
}

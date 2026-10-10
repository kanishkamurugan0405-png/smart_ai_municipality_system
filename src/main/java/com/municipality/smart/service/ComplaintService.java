package com.municipality.smart.service;

import com.municipality.smart.dto.*;
import com.municipality.smart.entity.*;
import com.municipality.smart.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ComplaintAnalysisRepository analysisRepository;
    private final RelatedComplaintRepository relatedComplaintRepository;
    private final HotspotRepository hotspotRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final ResolutionRepository resolutionRepository;

    private final AiAnalysisService aiAnalysisService;
    private final DuplicateDetectionService duplicateDetectionService;
    private final HotspotService hotspotService;
    private final PriorityScoringService priorityScoringService;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Autowired
    public ComplaintService(
            ComplaintRepository complaintRepository,
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            ComplaintAnalysisRepository analysisRepository,
            RelatedComplaintRepository relatedComplaintRepository,
            HotspotRepository hotspotRepository,
            StatusHistoryRepository statusHistoryRepository,
            ResolutionRepository resolutionRepository,
            AiAnalysisService aiAnalysisService,
            DuplicateDetectionService duplicateDetectionService,
            HotspotService hotspotService,
            PriorityScoringService priorityScoringService) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.analysisRepository = analysisRepository;
        this.relatedComplaintRepository = relatedComplaintRepository;
        this.hotspotRepository = hotspotRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.resolutionRepository = resolutionRepository;
        this.aiAnalysisService = aiAnalysisService;
        this.duplicateDetectionService = duplicateDetectionService;
        this.hotspotService = hotspotService;
        this.priorityScoringService = priorityScoringService;
    }

    @Transactional
    public ComplaintResponse createComplaint(Long userId, String description, String location,
                                             Double latitude, Double longitude, MultipartFile imageFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 1. Save uploaded image
        String storedImagePath = null;
        String originalFilename = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            originalFilename = imageFile.getOriginalFilename();
            try {
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                Files.createDirectories(uploadPath);

                String ext = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                } else {
                    ext = ".jpg";
                }
                String filename = "issue_" + UUID.randomUUID().toString().substring(0, 8) + ext;
                Path targetLocation = uploadPath.resolve(filename);
                Files.copy(imageFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                storedImagePath = "/uploads/" + filename;
            } catch (IOException e) {
                System.err.println("Failed to store image: " + e.getMessage());
            }
        }

        // 2. Initial Complaint Entity
        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setDescription(description != null ? description.trim() : "");
        complaint.setLocation(location != null ? location.trim() : "Unspecified Area");
        complaint.setLatitude(latitude);
        complaint.setLongitude(longitude);
        complaint.setImagePath(storedImagePath);
        complaint.setStatus("SUBMITTED");
        complaint.setCategory("Pending AI");
        complaint.setSeverity("MEDIUM");
        complaint.setPriority("MEDIUM");
        complaint.setIsHotspot(false);

        // Pre-save to have ID
        complaint = complaintRepository.save(complaint);

        // 3. AI Analysis
        String fsImagePath = storedImagePath != null ? Paths.get(uploadDir).resolve(Paths.get(storedImagePath).getFileName()).toString() : null;
        AiAnalysisResultDto aiDto = aiAnalysisService.analyzeIssue(description, fsImagePath, originalFilename);

        complaint.setCategory(aiDto.getCategory());
        complaint.setSeverity(aiDto.getSeverity());

        // Map AI Recommended Department
        Optional<Department> deptOpt = departmentRepository.findByNameIgnoreCase(aiDto.getDepartment());
        deptOpt.ifPresent(complaint::setDepartment);

        complaint = complaintRepository.save(complaint);

        // 4. Save Complaint Analysis Entity
        ComplaintAnalysis analysis = new ComplaintAnalysis(
                complaint,
                aiDto.getCategory(),
                aiDto.getSeverity(),
                aiDto.getDepartment(),
                aiDto.getConfidence(),
                aiDto.getAnalysisSource(),
                aiDto.getReasoning()
        );
        analysisRepository.save(analysis);

        // 5. Duplicate Detection
        List<RelatedComplaint> relatedList = duplicateDetectionService.detectAndLinkDuplicates(complaint);

        // 6. Civic Hotspot Evaluation
        Hotspot hotspot = hotspotService.evaluateHotspotForComplaint(complaint);
        if (hotspot != null) {
            complaint.setIsHotspot(true);
        }

        // 7. Calculate Priority Score
        String priority = priorityScoringService.calculatePriority(complaint, relatedList.size(), Boolean.TRUE.equals(complaint.getIsHotspot()));
        complaint.setPriority(priority);
        complaint = complaintRepository.save(complaint);

        // 8. Log Initial Status History
        StatusHistory initialHistory = new StatusHistory(
                complaint,
                "SUBMITTED",
                "Complaint lodged by citizen via Web Portal.",
                user.getName()
        );
        statusHistoryRepository.save(initialHistory);

        return mapToResponse(complaint);
    }

    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintById(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with id: " + complaintId));
        return mapToResponse(complaint);
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsByUser(Long userId) {
        return complaintRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> filterComplaints(String category, String severity, String status,
                                                    Long departmentId, String location, String search) {
        String catParam = (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL")) ? category.trim() : null;
        String sevParam = (severity != null && !severity.trim().isEmpty() && !severity.equalsIgnoreCase("ALL")) ? severity.trim() : null;
        String statParam = (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) ? status.trim() : null;
        String locParam = (location != null && !location.trim().isEmpty()) ? location.trim() : null;
        String searchParam = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        List<Complaint> complaints = complaintRepository.filterComplaints(catParam, sevParam, statParam, departmentId, locParam, searchParam);
        return complaints.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public ComplaintResponse assignDepartment(Long complaintId, Long departmentId, String comment, String officerName) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with id: " + complaintId));
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + departmentId));

        complaint.setDepartment(department);
        if ("SUBMITTED".equalsIgnoreCase(complaint.getStatus())) {
            complaint.setStatus("ASSIGNED");
        }
        complaint.setUpdatedAt(LocalDateTime.now());
        complaint = complaintRepository.save(complaint);

        String note = "Assigned to " + department.getName() + (comment != null && !comment.isBlank() ? ". Note: " + comment : "");
        StatusHistory history = new StatusHistory(complaint, complaint.getStatus(), note, officerName != null ? officerName : "Municipal Dispatcher");
        statusHistoryRepository.save(history);

        return mapToResponse(complaint);
    }

    @Transactional
    public ComplaintResponse updateStatus(Long complaintId, String newStatus, String comment, String officerName) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with id: " + complaintId));

        complaint.setStatus(newStatus.toUpperCase());
        complaint.setUpdatedAt(LocalDateTime.now());
        complaint = complaintRepository.save(complaint);

        String note = comment != null && !comment.isBlank() ? comment : "Status updated to " + newStatus;
        StatusHistory history = new StatusHistory(complaint, complaint.getStatus(), note, officerName != null ? officerName : "Municipal Officer");
        statusHistoryRepository.save(history);

        return mapToResponse(complaint);
    }

    @Transactional
    public ComplaintResponse resolveComplaint(Long complaintId, String resolutionNote, String resolvedBy) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with id: " + complaintId));

        complaint.setStatus("RESOLVED");
        complaint.setUpdatedAt(LocalDateTime.now());
        final Complaint savedComplaint = complaintRepository.save(complaint);

        // Save Resolution Entity
        Resolution resolution = resolutionRepository.findByComplaintId(complaintId)
                .orElseGet(() -> new Resolution(savedComplaint, resolutionNote, resolvedBy));
        resolution.setResolutionNote(resolutionNote);
        resolution.setResolvedBy(resolvedBy);
        resolution.setResolvedAt(LocalDateTime.now());
        resolutionRepository.save(resolution);

        // Add Status History
        StatusHistory history = new StatusHistory(savedComplaint, "RESOLVED", "Issue Resolved: " + resolutionNote, resolvedBy);
        statusHistoryRepository.save(history);

        return mapToResponse(savedComplaint);
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStatistics() {
        DashboardStatsDto stats = new DashboardStatsDto();

        List<Complaint> all = complaintRepository.findAll();
        stats.setTotalComplaints(all.size());
        stats.setPending(all.stream().filter(c -> "SUBMITTED".equalsIgnoreCase(c.getStatus())).count());
        stats.setAssigned(all.stream().filter(c -> "ASSIGNED".equalsIgnoreCase(c.getStatus())).count());
        stats.setInProgress(all.stream().filter(c -> "IN_PROGRESS".equalsIgnoreCase(c.getStatus())).count());
        stats.setResolved(all.stream().filter(c -> "RESOLVED".equalsIgnoreCase(c.getStatus())).count());
        stats.setHighPriority(all.stream().filter(c -> "HIGH".equalsIgnoreCase(c.getPriority()) || "CRITICAL".equalsIgnoreCase(c.getPriority())).count());
        stats.setCivicHotspots(hotspotRepository.countByStatus("ACTIVE"));

        // Category breakdown
        Map<String, Long> categoryCount = all.stream()
                .collect(Collectors.groupingBy(c -> c.getCategory() != null ? c.getCategory() : "Other", Collectors.counting()));
        stats.setCategoryBreakdown(categoryCount);

        // Severity breakdown
        Map<String, Long> severityCount = all.stream()
                .collect(Collectors.groupingBy(c -> c.getSeverity() != null ? c.getSeverity() : "MEDIUM", Collectors.counting()));
        stats.setSeverityBreakdown(severityCount);

        return stats;
    }

    public ComplaintResponse mapToResponse(Complaint c) {
        ComplaintResponse dto = new ComplaintResponse();
        dto.setId(c.getId());
        if (c.getUser() != null) {
            dto.setUserId(c.getUser().getId());
            dto.setCitizenName(c.getUser().getName());
            dto.setCitizenEmail(c.getUser().getEmail());
            dto.setCitizenPhone(c.getUser().getPhone());
        }

        dto.setDescription(c.getDescription());
        dto.setImagePath(c.getImagePath());
        dto.setLocation(c.getLocation());
        dto.setLatitude(c.getLatitude());
        dto.setLongitude(c.getLongitude());
        dto.setCategory(c.getCategory());
        dto.setSeverity(c.getSeverity());
        dto.setPriority(c.getPriority());
        dto.setStatus(c.getStatus());
        dto.setIsHotspot(c.getIsHotspot());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());

        if (c.getDepartment() != null) {
            dto.setDepartmentId(c.getDepartment().getId());
            dto.setDepartmentName(c.getDepartment().getName());
            dto.setDepartmentCode(c.getDepartment().getCode());
        }

        // AI Analysis
        analysisRepository.findByComplaintId(c.getId()).ifPresent(a -> {
            AiAnalysisResultDto aiDto = new AiAnalysisResultDto(
                    a.getCategory(), a.getSeverity(), a.getDepartment(),
                    a.getConfidence(), a.getAnalysisSource(), a.getReasoning()
            );
            dto.setAiAnalysis(aiDto);
        });

        // Related Complaints
        List<RelatedComplaint> links = relatedComplaintRepository.findByComplaintId(c.getId());
        List<RelatedComplaintDto> relatedDtos = new ArrayList<>();
        for (RelatedComplaint link : links) {
            Complaint rc = link.getRelatedComplaint();
            if (rc != null) {
                RelatedComplaintDto rd = new RelatedComplaintDto();
                rd.setId(link.getId());
                rd.setRelatedComplaintId(rc.getId());
                rd.setCategory(rc.getCategory());
                rd.setLocation(rc.getLocation());
                rd.setSeverity(rc.getSeverity());
                rd.setStatus(rc.getStatus());
                rd.setDescription(rc.getDescription());
                rd.setSimilarityScore(link.getSimilarityScore());
                rd.setMatchReason(link.getMatchReason());
                rd.setCreatedAt(rc.getCreatedAt());
                relatedDtos.add(rd);
            }
        }
        dto.setRelatedComplaints(relatedDtos);
        dto.setRelatedCount(relatedDtos.size());

        // Hotspot information if any
        hotspotRepository.findByLocationIgnoreCaseAndCategoryIgnoreCase(c.getLocation(), c.getCategory()).ifPresent(h -> {
            dto.setHotspot(new HotspotDto(
                    h.getId(), h.getLocation(), h.getCategory(), h.getReportCount(),
                    h.getSeverity(), h.getPriority(), h.getStatus(), h.getResponsibleDepartment(),
                    h.getLatitude(), h.getLongitude(), h.getUpdatedAt()
            ));
        });

        // Timeline
        List<StatusHistory> histories = statusHistoryRepository.findByComplaintIdOrderByCreatedAtAsc(c.getId());
        dto.setTimeline(histories.stream()
                .map(h -> new TimelineItemDto(h.getStatus(), h.getComment(), h.getUpdatedBy(), h.getCreatedAt()))
                .collect(Collectors.toList()));

        // Resolution
        resolutionRepository.findByComplaintId(c.getId()).ifPresent(res -> {
            dto.setResolutionNote(res.getResolutionNote());
            dto.setResolvedBy(res.getResolvedBy());
            dto.setResolvedAt(res.getResolvedAt());
        });

        return dto;
    }
}

package com.municipality.smart.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComplaintResponse {

    private Long id;
    private Long userId;
    private String citizenName;
    private String citizenEmail;
    private String citizenPhone;

    private String description;
    private String imagePath;
    private String location;
    private Double latitude;
    private Double longitude;

    private String category;
    private String severity;
    private String priority;
    private String status;
    private Boolean isHotspot;

    private Long departmentId;
    private String departmentName;
    private String departmentCode;

    private AiAnalysisResultDto aiAnalysis;
    private List<RelatedComplaintDto> relatedComplaints = new ArrayList<>();
    private Integer relatedCount = 0;

    private HotspotDto hotspot;
    private List<TimelineItemDto> timeline = new ArrayList<>();

    private String resolutionNote;
    private String resolvedBy;
    private LocalDateTime resolvedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ComplaintResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCitizenName() { return citizenName; }
    public void setCitizenName(String citizenName) { this.citizenName = citizenName; }

    public String getCitizenEmail() { return citizenEmail; }
    public void setCitizenEmail(String citizenEmail) { this.citizenEmail = citizenEmail; }

    public String getCitizenPhone() { return citizenPhone; }
    public void setCitizenPhone(String citizenPhone) { this.citizenPhone = citizenPhone; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getIsHotspot() { return isHotspot; }
    public void setIsHotspot(Boolean isHotspot) { this.isHotspot = isHotspot; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }

    public AiAnalysisResultDto getAiAnalysis() { return aiAnalysis; }
    public void setAiAnalysis(AiAnalysisResultDto aiAnalysis) { this.aiAnalysis = aiAnalysis; }

    public List<RelatedComplaintDto> getRelatedComplaints() { return relatedComplaints; }
    public void setRelatedComplaints(List<RelatedComplaintDto> relatedComplaints) { this.relatedComplaints = relatedComplaints; }

    public Integer getRelatedCount() { return relatedCount; }
    public void setRelatedCount(Integer relatedCount) { this.relatedCount = relatedCount; }

    public HotspotDto getHotspot() { return hotspot; }
    public void setHotspot(HotspotDto hotspot) { this.hotspot = hotspot; }

    public List<TimelineItemDto> getTimeline() { return timeline; }
    public void setTimeline(List<TimelineItemDto> timeline) { this.timeline = timeline; }

    public String getResolutionNote() { return resolutionNote; }
    public void setResolutionNote(String resolutionNote) { this.resolutionNote = resolutionNote; }

    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

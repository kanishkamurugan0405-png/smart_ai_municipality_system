package com.municipality.smart.dto;

import java.time.LocalDateTime;

public class HotspotDto {
    private Long id;
    private String location;
    private String category;
    private Integer reportCount;
    private String severity;
    private String priority;
    private String status;
    private String responsibleDepartment;
    private Double latitude;
    private Double longitude;
    private LocalDateTime updatedAt;

    public HotspotDto() {}

    public HotspotDto(Long id, String location, String category, Integer reportCount,
                      String severity, String priority, String status, String responsibleDepartment,
                      Double latitude, Double longitude, LocalDateTime updatedAt) {
        this.id = id;
        this.location = location;
        this.category = category;
        this.reportCount = reportCount;
        this.severity = severity;
        this.priority = priority;
        this.status = status;
        this.responsibleDepartment = responsibleDepartment;
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getReportCount() { return reportCount; }
    public void setReportCount(Integer reportCount) { this.reportCount = reportCount; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResponsibleDepartment() { return responsibleDepartment; }
    public void setResponsibleDepartment(String responsibleDepartment) { this.responsibleDepartment = responsibleDepartment; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

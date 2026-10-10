package com.municipality.smart.dto;

import java.time.LocalDateTime;

public class RelatedComplaintDto {
    private Long id;
    private Long relatedComplaintId;
    private String category;
    private String location;
    private String severity;
    private String status;
    private String description;
    private Double similarityScore;
    private String matchReason;
    private LocalDateTime createdAt;

    public RelatedComplaintDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRelatedComplaintId() { return relatedComplaintId; }
    public void setRelatedComplaintId(Long relatedComplaintId) { this.relatedComplaintId = relatedComplaintId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(Double similarityScore) { this.similarityScore = similarityScore; }

    public String getMatchReason() { return matchReason; }
    public void setMatchReason(String matchReason) { this.matchReason = matchReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

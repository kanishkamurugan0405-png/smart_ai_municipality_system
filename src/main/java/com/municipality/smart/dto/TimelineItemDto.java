package com.municipality.smart.dto;

import java.time.LocalDateTime;

public class TimelineItemDto {
    private String status;
    private String comment;
    private String updatedBy;
    private LocalDateTime createdAt;

    public TimelineItemDto() {}

    public TimelineItemDto(String status, String comment, String updatedBy, LocalDateTime createdAt) {
        this.status = status;
        this.comment = comment;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

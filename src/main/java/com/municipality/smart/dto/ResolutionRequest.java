package com.municipality.smart.dto;

import jakarta.validation.constraints.NotBlank;

public class ResolutionRequest {

    @NotBlank(message = "Resolution note is required")
    private String resolutionNote;

    @NotBlank(message = "Resolved by is required")
    private String resolvedBy;

    public ResolutionRequest() {}

    public ResolutionRequest(String resolutionNote, String resolvedBy) {
        this.resolutionNote = resolutionNote;
        this.resolvedBy = resolvedBy;
    }

    public String getResolutionNote() { return resolutionNote; }
    public void setResolutionNote(String resolutionNote) { this.resolutionNote = resolutionNote; }

    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }
}

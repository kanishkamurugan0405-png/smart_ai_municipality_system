package com.municipality.smart.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaint_analysis")
public class ComplaintAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private Integer confidence; // e.g. 92 (meaning 92%)

    @Column(name = "analysis_source", nullable = false)
    private String analysisSource; // "GEMINI_AI" or "RULE_ENGINE_MOCK"

    @Column(columnDefinition = "TEXT")
    private String reasoning;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ComplaintAnalysis() {}

    public ComplaintAnalysis(Complaint complaint, String category, String severity,
                             String department, Integer confidence, String analysisSource, String reasoning) {
        this.complaint = complaint;
        this.category = category;
        this.severity = severity;
        this.department = department;
        this.confidence = confidence;
        this.analysisSource = analysisSource;
        this.reasoning = reasoning;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    public String getAnalysisSource() {
        return analysisSource;
    }

    public void setAnalysisSource(String analysisSource) {
        this.analysisSource = analysisSource;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

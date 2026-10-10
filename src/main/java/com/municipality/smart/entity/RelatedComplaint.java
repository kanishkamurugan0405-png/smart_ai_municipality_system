package com.municipality.smart.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "related_complaints")
public class RelatedComplaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "related_complaint_id", nullable = false)
    private Complaint relatedComplaint;

    @Column(name = "similarity_score", nullable = false)
    private Double similarityScore; // 0.0 to 1.0 (e.g. 0.88)

    @Column(name = "match_reason")
    private String matchReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public RelatedComplaint() {}

    public RelatedComplaint(Complaint complaint, Complaint relatedComplaint, Double similarityScore, String matchReason) {
        this.complaint = complaint;
        this.relatedComplaint = relatedComplaint;
        this.similarityScore = similarityScore;
        this.matchReason = matchReason;
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

    public Complaint getRelatedComplaint() {
        return relatedComplaint;
    }

    public void setRelatedComplaint(Complaint relatedComplaint) {
        this.relatedComplaint = relatedComplaint;
    }

    public Double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(Double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

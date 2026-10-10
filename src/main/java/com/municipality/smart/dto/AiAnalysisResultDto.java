package com.municipality.smart.dto;

public class AiAnalysisResultDto {
    private String category;
    private String severity;
    private String department;
    private Integer confidence;
    private String analysisSource; // "GEMINI_AI" or "RULE_ENGINE_MOCK"
    private String reasoning;

    public AiAnalysisResultDto() {}

    public AiAnalysisResultDto(String category, String severity, String department, Integer confidence, String analysisSource, String reasoning) {
        this.category = category;
        this.severity = severity;
        this.department = department;
        this.confidence = confidence;
        this.analysisSource = analysisSource;
        this.reasoning = reasoning;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getConfidence() { return confidence; }
    public void setConfidence(Integer confidence) { this.confidence = confidence; }

    public String getAnalysisSource() { return analysisSource; }
    public void setAnalysisSource(String analysisSource) { this.analysisSource = analysisSource; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
}

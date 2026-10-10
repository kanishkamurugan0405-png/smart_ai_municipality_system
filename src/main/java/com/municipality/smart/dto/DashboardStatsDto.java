package com.municipality.smart.dto;

import java.util.Map;

public class DashboardStatsDto {
    private long totalComplaints;
    private long pending;
    private long assigned;
    private long inProgress;
    private long resolved;
    private long highPriority;
    private long civicHotspots;
    private Map<String, Long> categoryBreakdown;
    private Map<String, Long> severityBreakdown;

    public DashboardStatsDto() {}

    public long getTotalComplaints() { return totalComplaints; }
    public void setTotalComplaints(long totalComplaints) { this.totalComplaints = totalComplaints; }

    public long getPending() { return pending; }
    public void setPending(long pending) { this.pending = pending; }

    public long getAssigned() { return assigned; }
    public void setAssigned(long assigned) { this.assigned = assigned; }

    public long getInProgress() { return inProgress; }
    public void setInProgress(long inProgress) { this.inProgress = inProgress; }

    public long getResolved() { return resolved; }
    public void setResolved(long resolved) { this.resolved = resolved; }

    public long getHighPriority() { return highPriority; }
    public void setHighPriority(long highPriority) { this.highPriority = highPriority; }

    public long getCivicHotspots() { return civicHotspots; }
    public void setCivicHotspots(long civicHotspots) { this.civicHotspots = civicHotspots; }

    public Map<String, Long> getCategoryBreakdown() { return categoryBreakdown; }
    public void setCategoryBreakdown(Map<String, Long> categoryBreakdown) { this.categoryBreakdown = categoryBreakdown; }

    public Map<String, Long> getSeverityBreakdown() { return severityBreakdown; }
    public void setSeverityBreakdown(Map<String, Long> severityBreakdown) { this.severityBreakdown = severityBreakdown; }
}

package com.municipality.smart.dto;

import jakarta.validation.constraints.NotNull;

public class AssignDepartmentRequest {

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private String comment;
    private String officerName;

    public AssignDepartmentRequest() {}

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getOfficerName() { return officerName; }
    public void setOfficerName(String officerName) { this.officerName = officerName; }
}

package com.municipality.smart.controller;

import com.municipality.smart.dto.*;
import com.municipality.smart.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    @Autowired
    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createComplaint(
            @RequestParam("userId") Long userId,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Complaint description cannot be empty"));
            }
            if (location == null || location.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Location cannot be empty"));
            }

            ComplaintResponse response = complaintService.createComplaint(
                    userId, description, location, latitude, longitude, image);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to submit complaint: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "severity", required = false) String severity,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "search", required = false) String search) {
        List<ComplaintResponse> list = complaintService.filterComplaints(
                category, severity, status, departmentId, location, search);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaintById(@PathVariable("id") Long id) {
        try {
            ComplaintResponse response = complaintService.getComplaintById(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ComplaintResponse>> getComplaintsByUser(@PathVariable("userId") Long userId) {
        List<ComplaintResponse> list = complaintService.getComplaintsByUser(userId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}/analysis")
    public ResponseEntity<?> getAnalysis(@PathVariable("id") Long id) {
        try {
            ComplaintResponse response = complaintService.getComplaintById(id);
            if (response.getAiAnalysis() == null) {
                return ResponseEntity.status(404).body(Map.of("error", "AI Analysis not found for this complaint"));
            }
            return ResponseEntity.ok(response.getAiAnalysis());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<?> getRelated(@PathVariable("id") Long id) {
        try {
            ComplaintResponse response = complaintService.getComplaintById(id);
            return ResponseEntity.ok(response.getRelatedComplaints());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignDepartment(
            @PathVariable("id") Long id,
            @Valid @RequestBody AssignDepartmentRequest request) {
        try {
            ComplaintResponse response = complaintService.assignDepartment(
                    id, request.getDepartmentId(), request.getComment(), request.getOfficerName());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        try {
            ComplaintResponse response = complaintService.updateStatus(
                    id, request.getStatus(), request.getComment(), request.getOfficerName());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolveComplaint(
            @PathVariable("id") Long id,
            @Valid @RequestBody ResolutionRequest request) {
        try {
            ComplaintResponse response = complaintService.resolveComplaint(
                    id, request.getResolutionNote(), request.getResolvedBy());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

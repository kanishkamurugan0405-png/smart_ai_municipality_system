package com.municipality.smart.controller;

import com.municipality.smart.dto.DashboardStatsDto;
import com.municipality.smart.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ComplaintService complaintService;

    @Autowired
    public DashboardController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<DashboardStatsDto> getDashboardStatistics() {
        return ResponseEntity.ok(complaintService.getDashboardStatistics());
    }
}

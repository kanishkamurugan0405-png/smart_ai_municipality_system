package com.municipality.smart.controller;

import com.municipality.smart.entity.Hotspot;
import com.municipality.smart.service.HotspotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hotspots")
public class HotspotController {

    private final HotspotService hotspotService;

    @Autowired
    public HotspotController(HotspotService hotspotService) {
        this.hotspotService = hotspotService;
    }

    @GetMapping
    public ResponseEntity<List<Hotspot>> getAllHotspots() {
        return ResponseEntity.ok(hotspotService.getAllHotspots());
    }
}

package com.municipality.smart.repository;

import com.municipality.smart.entity.Hotspot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotspotRepository extends JpaRepository<Hotspot, Long> {
    List<Hotspot> findAllByOrderByReportCountDesc();
    Optional<Hotspot> findByLocationIgnoreCaseAndCategoryIgnoreCase(String location, String category);
    long countByStatus(String status);
}

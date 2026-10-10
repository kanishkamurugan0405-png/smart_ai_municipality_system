package com.municipality.smart.repository;

import com.municipality.smart.entity.ComplaintAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComplaintAnalysisRepository extends JpaRepository<ComplaintAnalysis, Long> {
    Optional<ComplaintAnalysis> findByComplaintId(Long complaintId);
}

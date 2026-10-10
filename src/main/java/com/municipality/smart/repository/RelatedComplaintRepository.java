package com.municipality.smart.repository;

import com.municipality.smart.entity.RelatedComplaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelatedComplaintRepository extends JpaRepository<RelatedComplaint, Long> {
    List<RelatedComplaint> findByComplaintId(Long complaintId);
    List<RelatedComplaint> findByRelatedComplaintId(Long relatedComplaintId);
    boolean existsByComplaintIdAndRelatedComplaintId(Long complaintId, Long relatedComplaintId);
}

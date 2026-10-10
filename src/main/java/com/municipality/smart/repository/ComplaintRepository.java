package com.municipality.smart.repository;

import com.municipality.smart.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Complaint> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);

    long countByPriority(String priority);

    List<Complaint> findByCategoryIgnoreCaseAndLocationIgnoreCase(String category, String location);

    List<Complaint> findByLocationIgnoreCase(String location);

    @Query("SELECT c FROM Complaint c WHERE " +
           "(:category IS NULL OR LOWER(c.category) = LOWER(:category)) AND " +
           "(:severity IS NULL OR LOWER(c.severity) = LOWER(:severity)) AND " +
           "(:status IS NULL OR LOWER(c.status) = LOWER(:status)) AND " +
           "(:departmentId IS NULL OR (c.department IS NOT NULL AND c.department.id = :departmentId)) AND " +
           "(:location IS NULL OR LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:search IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.location) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.category) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY " +
           "CASE WHEN c.priority = 'CRITICAL' THEN 1 " +
           "     WHEN c.priority = 'HIGH' THEN 2 " +
           "     WHEN c.priority = 'MEDIUM' THEN 3 " +
           "     ELSE 4 END, c.createdAt DESC")
    List<Complaint> filterComplaints(
            @Param("category") String category,
            @Param("severity") String severity,
            @Param("status") String status,
            @Param("departmentId") Long departmentId,
            @Param("location") String location,
            @Param("search") String search);
}

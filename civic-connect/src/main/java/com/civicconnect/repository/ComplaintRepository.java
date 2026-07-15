package com.civicconnect.repository;

import com.civicconnect.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByCitizenUserIdOrderByCreatedAtDesc(Long citizenId);
    List<Complaint> findByStatusOrderByCreatedAtDesc(Complaint.Status status);
    List<Complaint> findAllByOrderByCreatedAtDesc();
    long countByStatus(Complaint.Status status);
}

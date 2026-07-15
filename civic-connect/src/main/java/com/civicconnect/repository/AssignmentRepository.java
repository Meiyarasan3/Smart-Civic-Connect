package com.civicconnect.repository;

import com.civicconnect.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    Optional<Assignment> findByComplaintComplaintId(Long complaintId);
    List<Assignment> findByAssignedToUserIdOrderByAssignedDateDesc(Long officerId);
}

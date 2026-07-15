package com.civicconnect.repository;

import com.civicconnect.entity.ComplaintUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintUpdateRepository extends JpaRepository<ComplaintUpdate, Long> {
    List<ComplaintUpdate> findByComplaintComplaintIdOrderByUpdateDateDesc(Long complaintId);
}

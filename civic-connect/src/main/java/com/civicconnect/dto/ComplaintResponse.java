package com.civicconnect.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComplaintResponse {
    private Long complaintId;
    private String title;
    private String description;
    private String category;
    private String location;
    private String imageUrl;
    private String status;
    private String citizenName;
    private Long citizenId;
    private String departmentName;
    private String assignedOfficerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<UpdateResponse> updates;
}

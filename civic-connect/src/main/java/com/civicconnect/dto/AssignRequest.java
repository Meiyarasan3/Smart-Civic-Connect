package com.civicconnect.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AssignRequest {
    @NotNull(message = "Complaint ID is required")
    private Long complaintId;

    @NotBlank(message = "Department name is required")
    private String departmentName;

    private Long assignedToOfficerId;
}

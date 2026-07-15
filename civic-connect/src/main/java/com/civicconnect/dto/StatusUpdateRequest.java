package com.civicconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StatusUpdateRequest {
    @NotBlank(message = "Status is required")
    private String status;

    private String remarks;
    private String proofImageUrl;
}

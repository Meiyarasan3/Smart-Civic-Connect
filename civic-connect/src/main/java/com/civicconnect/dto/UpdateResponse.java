package com.civicconnect.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateResponse {
    private Long updateId;
    private String officerName;
    private String remarks;
    private String status;
    private String proofImageUrl;
    private LocalDateTime updateDate;
}

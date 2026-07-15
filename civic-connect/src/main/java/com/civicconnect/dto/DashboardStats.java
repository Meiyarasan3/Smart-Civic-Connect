package com.civicconnect.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStats {
    private long total;
    private long submitted;
    private long assigned;
    private long inProgress;
    private long resolved;
    private long verified;
    private long reopened;
}

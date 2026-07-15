package com.civicconnect.controller;

import com.civicconnect.config.JwtUtil;
import com.civicconnect.dto.*;
import com.civicconnect.service.ComplaintService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ComplaintService complaintService;
    private final JwtUtil jwtUtil;

    // View all complaints
    @GetMapping("/complaints")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> allComplaints() {
        return ResponseEntity.ok(ApiResponse.ok("All complaints", complaintService.getAllComplaints()));
    }

    // Filter complaints by status
    @GetMapping("/complaints/filter")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> filterByStatus(
            @RequestParam String status) {
        return ResponseEntity.ok(
                ApiResponse.ok("Filtered complaints", complaintService.getComplaintsByStatus(status)));
    }

    // Dashboard statistics
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStats>> stats() {
        return ResponseEntity.ok(ApiResponse.ok("Dashboard stats", complaintService.getStats()));
    }

    // Assign complaint to department
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<ComplaintResponse>> assign(
            @Valid @RequestBody AssignRequest req,
            HttpServletRequest httpReq) {
        Long adminId = extractUserId(httpReq);
        ComplaintResponse response = complaintService.assignComplaint(adminId, req);
        return ResponseEntity.ok(ApiResponse.ok("Complaint assigned successfully", response));
    }

    // Get complaint detail
    @GetMapping("/complaints/{id}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> detail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Complaint detail", complaintService.getComplaintDetail(id)));
    }

    // List department officers (for assignment dropdown)
    @GetMapping("/officers")
    public ResponseEntity<ApiResponse<List<AuthResponse>>> officers() {
        return ResponseEntity.ok(ApiResponse.ok("Department officers", complaintService.getDepartmentOfficers()));
    }

    private Long extractUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = header.substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }
}

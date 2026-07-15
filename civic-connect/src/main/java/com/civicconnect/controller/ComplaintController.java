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
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;
    private final JwtUtil jwtUtil;

    // Citizen submits a new complaint
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ComplaintResponse>> submit(
            @Valid @RequestBody ComplaintRequest req,
            HttpServletRequest httpReq) {
        Long userId = extractUserId(httpReq);
        ComplaintResponse response = complaintService.submitComplaint(userId, req);
        return ResponseEntity.ok(ApiResponse.ok("Complaint submitted successfully", response));
    }

    // Citizen views their own complaints
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> myComplaints(HttpServletRequest httpReq) {
        Long userId = extractUserId(httpReq);
        List<ComplaintResponse> complaints = complaintService.getMyComplaints(userId);
        return ResponseEntity.ok(ApiResponse.ok("Your complaints", complaints));
    }

    // Citizen views a specific complaint detail
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> detail(@PathVariable Long id) {
        ComplaintResponse response = complaintService.getComplaintDetail(id);
        return ResponseEntity.ok(ApiResponse.ok("Complaint details", response));
    }

    // Citizen verifies a resolved complaint
    @PutMapping("/{id}/verify")
    public ResponseEntity<ApiResponse<ComplaintResponse>> verify(
            @PathVariable Long id,
            HttpServletRequest httpReq) {
        Long userId = extractUserId(httpReq);
        ComplaintResponse response = complaintService.verifyComplaint(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Complaint verified. Thank you!", response));
    }

    // Citizen reopens a resolved complaint
    @PutMapping("/{id}/reopen")
    public ResponseEntity<ApiResponse<ComplaintResponse>> reopen(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            HttpServletRequest httpReq) {
        Long userId = extractUserId(httpReq);
        String reason = body != null ? body.get("reason") : null;
        ComplaintResponse response = complaintService.reopenComplaint(id, userId, reason);
        return ResponseEntity.ok(ApiResponse.ok("Complaint reopened", response));
    }

    // ── Helper ──
    private Long extractUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = header.substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }
}

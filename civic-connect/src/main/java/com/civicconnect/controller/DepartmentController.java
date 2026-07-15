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
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final ComplaintService complaintService;
    private final JwtUtil jwtUtil;

    // View complaints assigned to this officer
    @GetMapping("/complaints")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> myAssigned(HttpServletRequest httpReq) {
        Long officerId = extractUserId(httpReq);
        return ResponseEntity.ok(
                ApiResponse.ok("Assigned complaints", complaintService.getAssignedComplaints(officerId)));
    }

    // View single complaint detail
    @GetMapping("/complaints/{id}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> detail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Complaint detail", complaintService.getComplaintDetail(id)));
    }

    // Update complaint status (IN_PROGRESS or RESOLVED)
    @PutMapping("/complaints/{id}/update")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest req,
            HttpServletRequest httpReq) {
        Long officerId = extractUserId(httpReq);
        ComplaintResponse response = complaintService.updateComplaintStatus(id, officerId, req);
        return ResponseEntity.ok(ApiResponse.ok("Status updated", response));
    }

    private Long extractUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = header.substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }
}

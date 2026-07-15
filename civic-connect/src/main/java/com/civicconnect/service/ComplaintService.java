package com.civicconnect.service;

import com.civicconnect.dto.*;
import com.civicconnect.entity.*;
import com.civicconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepo;
    private final UserRepository userRepo;
    private final AssignmentRepository assignmentRepo;
    private final ComplaintUpdateRepository updateRepo;

    // ────── CITIZEN: Submit complaint ──────

    @Transactional
    public ComplaintResponse submitComplaint(Long citizenId, ComplaintRequest req) {
        User citizen = userRepo.findById(citizenId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Complaint complaint = new Complaint();
        complaint.setCitizen(citizen);
        complaint.setTitle(req.getTitle());
        complaint.setDescription(req.getDescription());
        complaint.setCategory(Complaint.Category.valueOf(req.getCategory()));
        complaint.setLocation(req.getLocation());
        complaint.setImageUrl(req.getImageUrl());
        complaint.setStatus(Complaint.Status.SUBMITTED);

        complaint = complaintRepo.save(complaint);
        return toResponse(complaint);
    }

    // ────── CITIZEN: View own complaints ──────

    public List<ComplaintResponse> getMyComplaints(Long citizenId) {
        return complaintRepo.findByCitizenUserIdOrderByCreatedAtDesc(citizenId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ────── CITIZEN: Verify resolved complaint ──────

    @Transactional
    public ComplaintResponse verifyComplaint(Long complaintId, Long citizenId) {
        Complaint complaint = getComplaintOwnedBy(complaintId, citizenId);

        if (complaint.getStatus() != Complaint.Status.RESOLVED) {
            throw new RuntimeException("Only resolved complaints can be verified");
        }

        complaint.setStatus(Complaint.Status.VERIFIED);
        complaintRepo.save(complaint);
        return toResponse(complaint);
    }

    // ────── CITIZEN: Reopen complaint ──────

    @Transactional
    public ComplaintResponse reopenComplaint(Long complaintId, Long citizenId, String reason) {
        Complaint complaint = getComplaintOwnedBy(complaintId, citizenId);

        if (complaint.getStatus() != Complaint.Status.RESOLVED) {
            throw new RuntimeException("Only resolved complaints can be reopened");
        }

        complaint.setStatus(Complaint.Status.REOPENED);
        complaintRepo.save(complaint);

        // Log the reopen as an update
        User citizen = userRepo.findById(citizenId).orElseThrow();
        ComplaintUpdate update = new ComplaintUpdate();
        update.setComplaint(complaint);
        update.setOfficer(citizen);
        update.setRemarks("Reopened by citizen: " + (reason != null ? reason : "Not satisfied with resolution"));
        update.setStatus(Complaint.Status.REOPENED);
        updateRepo.save(update);

        return toResponse(complaint);
    }

    // ────── ADMIN: View all complaints ──────

    public List<ComplaintResponse> getAllComplaints() {
        return complaintRepo.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ────── ADMIN: Filter by status ──────

    public List<ComplaintResponse> getComplaintsByStatus(String status) {
        Complaint.Status s = Complaint.Status.valueOf(status);
        return complaintRepo.findByStatusOrderByCreatedAtDesc(s)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ────── ADMIN: Assign complaint to department ──────

    @Transactional
    public ComplaintResponse assignComplaint(Long adminId, AssignRequest req) {
        Complaint complaint = complaintRepo.findById(req.getComplaintId())
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        User admin = userRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Create or update assignment
        Assignment assignment = assignmentRepo
                .findByComplaintComplaintId(complaint.getComplaintId())
                .orElse(new Assignment());

        assignment.setComplaint(complaint);
        assignment.setDepartmentName(req.getDepartmentName());
        assignment.setAssignedBy(admin);

        if (req.getAssignedToOfficerId() != null) {
            User officer = userRepo.findById(req.getAssignedToOfficerId())
                    .orElseThrow(() -> new RuntimeException("Officer not found"));
            assignment.setAssignedTo(officer);
        }

        assignmentRepo.save(assignment);

        // Update complaint status
        complaint.setStatus(Complaint.Status.ASSIGNED);
        complaintRepo.save(complaint);

        return toResponse(complaint);
    }

    // ────── ADMIN: Dashboard stats ──────

    public DashboardStats getStats() {
        return DashboardStats.builder()
                .total(complaintRepo.count())
                .submitted(complaintRepo.countByStatus(Complaint.Status.SUBMITTED))
                .assigned(complaintRepo.countByStatus(Complaint.Status.ASSIGNED))
                .inProgress(complaintRepo.countByStatus(Complaint.Status.IN_PROGRESS))
                .resolved(complaintRepo.countByStatus(Complaint.Status.RESOLVED))
                .verified(complaintRepo.countByStatus(Complaint.Status.VERIFIED))
                .reopened(complaintRepo.countByStatus(Complaint.Status.REOPENED))
                .build();
    }

    // ────── DEPARTMENT: View assigned complaints ──────

    public List<ComplaintResponse> getAssignedComplaints(Long officerId) {
        return assignmentRepo.findByAssignedToUserIdOrderByAssignedDateDesc(officerId)
                .stream()
                .map(a -> toResponse(a.getComplaint()))
                .collect(Collectors.toList());
    }

    // ────── DEPARTMENT: Update complaint status ──────

    @Transactional
    public ComplaintResponse updateComplaintStatus(Long complaintId, Long officerId,
                                                    StatusUpdateRequest req) {
        Complaint complaint = complaintRepo.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        User officer = userRepo.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Officer not found"));

        Complaint.Status newStatus = Complaint.Status.valueOf(req.getStatus());
        complaint.setStatus(newStatus);
        complaintRepo.save(complaint);

        // Log the update
        ComplaintUpdate update = new ComplaintUpdate();
        update.setComplaint(complaint);
        update.setOfficer(officer);
        update.setRemarks(req.getRemarks());
        update.setStatus(newStatus);
        update.setProofImageUrl(req.getProofImageUrl());
        updateRepo.save(update);

        return toResponse(complaint);
    }

    // ────── VIEW: Single complaint detail ──────

    public ComplaintResponse getComplaintDetail(Long complaintId) {
        Complaint complaint = complaintRepo.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        return toResponse(complaint);
    }

    // ────── ADMIN: Get department officers ──────

    public List<AuthResponse> getDepartmentOfficers() {
        return userRepo.findByRole(User.Role.DEPARTMENT).stream()
                .map(u -> AuthResponse.builder()
                        .userId(u.getUserId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .role(u.getRole().name())
                        .build())
                .collect(Collectors.toList());
    }

    // ────── HELPERS ──────

    private Complaint getComplaintOwnedBy(Long complaintId, Long citizenId) {
        Complaint complaint = complaintRepo.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        if (!complaint.getCitizen().getUserId().equals(citizenId)) {
            throw new RuntimeException("You can only manage your own complaints");
        }
        return complaint;
    }

    private ComplaintResponse toResponse(Complaint c) {
        // Get assignment info if exists
        String deptName = null;
        String officerName = null;
        var assignment = assignmentRepo.findByComplaintComplaintId(c.getComplaintId());
        if (assignment.isPresent()) {
            deptName = assignment.get().getDepartmentName();
            if (assignment.get().getAssignedTo() != null) {
                officerName = assignment.get().getAssignedTo().getName();
            }
        }

        // Get updates
        List<UpdateResponse> updates = updateRepo
                .findByComplaintComplaintIdOrderByUpdateDateDesc(c.getComplaintId())
                .stream()
                .map(u -> UpdateResponse.builder()
                        .updateId(u.getUpdateId())
                        .officerName(u.getOfficer().getName())
                        .remarks(u.getRemarks())
                        .status(u.getStatus().name())
                        .proofImageUrl(u.getProofImageUrl())
                        .updateDate(u.getUpdateDate())
                        .build())
                .collect(Collectors.toList());

        return ComplaintResponse.builder()
                .complaintId(c.getComplaintId())
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getCategory().name())
                .location(c.getLocation())
                .imageUrl(c.getImageUrl())
                .status(c.getStatus().name())
                .citizenName(c.getCitizen().getName())
                .citizenId(c.getCitizen().getUserId())
                .departmentName(deptName)
                .assignedOfficerName(officerName)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .updates(updates)
                .build();
    }
}

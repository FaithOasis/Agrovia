package com.agrosolutions.AgroVia.controller;
import com.agrosolutions.AgroVia.dto.*;
import com.agrosolutions.AgroVia.service.ParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/participations")
@CrossOrigin(origins = "*")
public class ParticipationController {
    @Autowired
    private ParticipationService participationService;

    // Join an acquisition
    @PostMapping("/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ParticipantSummary>> joinAcquisition(
            @Valid @RequestBody JoinAcquisitionRequest request) {
        ParticipantSummary response = participationService.joinAcquisition(request);
        return new ResponseEntity<>(
                ApiResponse.success("Successfully joined acquisition", response),
                HttpStatus.CREATED
        );
    }

    // Approve participant (creator only)
    @PostMapping("/{participantId}/approve")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ParticipantSummary>> approveParticipant(
            @PathVariable Long participantId) {
        ParticipantSummary response = participationService.approveParticipant(participantId);
        return ResponseEntity.ok(
                ApiResponse.success("Participant approved successfully", response)
        );
    }

    // Reject participant (creator only)
    @PostMapping("/{participantId}/reject")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ParticipantSummary>> rejectParticipant(
            @PathVariable Long participantId,
            @RequestParam(required = false) String reason) {
        ParticipantSummary response = participationService.rejectParticipant(participantId, reason);
        return ResponseEntity.ok(
                ApiResponse.success("Participant rejected successfully", response)
        );
    }

    // Leave acquisition (withdraw)
    @PostMapping("/acquisitions/{acquisitionId}/leave")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> leaveAcquisition(@PathVariable Long acquisitionId) {
        participationService.leaveAcquisition(acquisitionId);
        return ResponseEntity.ok(
                ApiResponse.success("Successfully left the acquisition", null)
        );
    }

    // Get my participations (acquisitions I've joined)
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<AcquisitionSummary>>> getMyParticipations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "joinedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AcquisitionSummary> participations = participationService.getMyParticipations(pageable);
        return ResponseEntity.ok(ApiResponse.success(participations));
    }
}

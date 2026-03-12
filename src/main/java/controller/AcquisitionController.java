package controller;

import com.agrosolutions.AgroVia.dto.*;
import service.AcquisitionService;
import service.ParticipationService;
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
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/acquisitions")
@CrossOrigin(origins = "*")
public class AcquisitionController {

    @Autowired
    private AcquisitionService acquisitionService;

    @Autowired
    private ParticipationService participationService;

    // Create new acquisition proposal
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AcquisitionResponse>> createAcquisition(
            @Valid @RequestBody AcquisitionRequest request) {
        AcquisitionResponse response = acquisitionService.createAcquisition(request);
        return new ResponseEntity<>(
                ApiResponse.success("Acquisition proposal created successfully", response),
                HttpStatus.CREATED
        );
    }

    // Publish acquisition (DRAFT → OPEN)
    @PostMapping("/{id}/publish")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AcquisitionResponse>> publishAcquisition(@PathVariable Long id) {
        AcquisitionResponse response = acquisitionService.publishAcquisition(id);
        return ResponseEntity.ok(
                ApiResponse.success("Acquisition published successfully", response)
        );
    }

    // Get all open acquisitions (public)
    @GetMapping("/open")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<Page<AcquisitionSummary>>> getOpenAcquisitions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AcquisitionSummary> acquisitions = acquisitionService.getOpenAcquisitions(pageable);
        return ResponseEntity.ok(ApiResponse.success(acquisitions));
    }

    // Get acquisition by ID (public)
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<AcquisitionResponse>> getAcquisitionById(@PathVariable Long id) {
        AcquisitionResponse response = acquisitionService.getAcquisitionById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Update acquisition
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AcquisitionResponse>> updateAcquisition(
            @PathVariable Long id,
            @Valid @RequestBody AcquisitionRequest request) {
        AcquisitionResponse response = acquisitionService.updateAcquisition(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Acquisition updated successfully", response)
        );
    }

    // Close acquisition (stop accepting participants)
    @PostMapping("/{id}/close")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AcquisitionResponse>> closeAcquisition(@PathVariable Long id) {
        AcquisitionResponse response = acquisitionService.closeAcquisition(id);
        return ResponseEntity.ok(
                ApiResponse.success("Acquisition closed successfully", response)
        );
    }

    // Cancel acquisition
    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AcquisitionResponse>> cancelAcquisition(@PathVariable Long id) {
        AcquisitionResponse response = acquisitionService.cancelAcquisition(id);
        return ResponseEntity.ok(
                ApiResponse.success("Acquisition cancelled successfully", response)
        );
    }

    // Get my acquisitions (as creator)
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<AcquisitionSummary>>> getMyAcquisitions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AcquisitionSummary> acquisitions = acquisitionService.getMyAcquisitions(pageable);
        return ResponseEntity.ok(ApiResponse.success(acquisitions));
    }

    // Search acquisitions with filters (public)
    @GetMapping("/search")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<Page<AcquisitionSummary>>> searchAcquisitions(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AcquisitionSummary> acquisitions = acquisitionService.searchAcquisitions(
                type, status, minPrice, maxPrice, location, searchTerm, pageable);
        return ResponseEntity.ok(ApiResponse.success(acquisitions));
    }

    // Get participants of an acquisition (creator only)
    @GetMapping("/{id}/participants")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<java.util.List<ParticipantSummary>>> getAcquisitionParticipants(
            @PathVariable Long id) {
        java.util.List<ParticipantSummary> participants = participationService.getAcquisitionParticipants(id);
        return ResponseEntity.ok(ApiResponse.success(participants));
    }
}

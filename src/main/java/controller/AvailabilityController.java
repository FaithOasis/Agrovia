package controller;

import com.agrosolutions.AgroVia.dto.*;
import service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;  // ADD THIS IMPORT
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@CrossOrigin(origins = "*")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    // Add recurring availability (weekly schedule)
    @PostMapping("/resources/{resourceId}/recurring")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ResourceAvailabilityResponse>>> addRecurringAvailability(
            @PathVariable Long resourceId,
            @RequestBody List<ResourceAvailabilityRequest> requests) {
        List<ResourceAvailabilityResponse> responses =
                availabilityService.addRecurringAvailability(resourceId, requests);
        return new ResponseEntity<>(
                ApiResponse.success("Recurring availability added successfully", responses),
                HttpStatus.CREATED
        );
    }

    // Add custom date range availability
    @PostMapping("/resources/{resourceId}/custom")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ResourceAvailabilityResponse>>> addCustomAvailability(
            @PathVariable Long resourceId,
            @RequestBody List<ResourceAvailabilityRequest> requests) {
        List<ResourceAvailabilityResponse> responses =
                availabilityService.addCustomAvailability(resourceId, requests);
        return new ResponseEntity<>(
                ApiResponse.success("Custom availability added successfully", responses),
                HttpStatus.CREATED
        );
    }

    // Get all availability for a resource (public)
    @GetMapping("/resources/{resourceId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<ResourceAvailabilityResponse>>> getResourceAvailability(
            @PathVariable Long resourceId) {
        List<ResourceAvailabilityResponse> responses =
                availabilityService.getResourceAvailability(resourceId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // Get available time slots for a date range (public)
    @GetMapping("/resources/{resourceId}/slots")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<AvailableSlotResponse>>> getAvailableSlots(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AvailableSlotResponse> slots =
                availabilityService.getAvailableSlots(resourceId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(slots));
    }

    // Update availability slot
    @PutMapping("/{availabilityId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceAvailabilityResponse>> updateAvailability(
            @PathVariable Long availabilityId,
            @RequestBody ResourceAvailabilityRequest request) {
        ResourceAvailabilityResponse response =
                availabilityService.updateAvailability(availabilityId, request);
        return ResponseEntity.ok(
                ApiResponse.success("Availability updated successfully", response)
        );
    }

    // Delete availability slot
    @DeleteMapping("/{availabilityId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteAvailability(@PathVariable Long availabilityId) {
        availabilityService.deleteAvailability(availabilityId);
        return ResponseEntity.ok(
                ApiResponse.success("Availability deleted successfully", null)
        );
    }

    // Check if resource is available at a specific time (public)
    @GetMapping("/resources/{resourceId}/check")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        boolean isAvailable = availabilityService.isResourceAvailable(resourceId, dateTime);
        return ResponseEntity.ok(ApiResponse.success(isAvailable));
    }
}
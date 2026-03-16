package com.agrosolutions.AgroVia.controller;
import com.agrosolutions.AgroVia.dto.*;
import com.agrosolutions.AgroVia.service.BookingService;
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
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    // Create new booking request
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceBookingResponse>> createBooking(
            @Valid @RequestBody ResourceBookingRequest request) {
        ResourceBookingResponse response = bookingService.createBooking(request);
        return new ResponseEntity<>(
                ApiResponse.success("Booking request created successfully", response),
                HttpStatus.CREATED
        );
    }

    // Get my bookings (as renter)
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ResourceBookingResponse>>> getMyBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ResourceBookingResponse> bookings = bookingService.getMyBookings(pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    // Get bookings for my resources (as owner)
    @GetMapping("/received")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ResourceBookingResponse>>> getReceivedBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ResourceBookingResponse> bookings = bookingService.getReceivedBookings(pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    // Get booking by ID
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceBookingResponse>> getBookingById(@PathVariable Long id) {
        ResourceBookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Confirm booking (owner action)
    @PutMapping("/{id}/confirm")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceBookingResponse>> confirmBooking(@PathVariable Long id) {
        ResourceBookingResponse response = bookingService.confirmBooking(id);
        return ResponseEntity.ok(
                ApiResponse.success("Booking confirmed successfully", response)
        );
    }

    // Cancel booking
    @PutMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceBookingResponse>> cancelBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        ResourceBookingResponse response = bookingService.cancelBooking(id, reason);
        return ResponseEntity.ok(
                ApiResponse.success("Booking cancelled successfully", response)
        );
    }

    // Start booking (mark as active)
    @PutMapping("/{id}/start")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceBookingResponse>> startBooking(@PathVariable Long id) {
        ResourceBookingResponse response = bookingService.startBooking(id);
        return ResponseEntity.ok(
                ApiResponse.success("Booking started successfully", response)
        );
    }

    // Complete booking
    @PutMapping("/{id}/complete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceBookingResponse>> completeBooking(@PathVariable Long id) {
        ResourceBookingResponse response = bookingService.completeBooking(id);
        return ResponseEntity.ok(
                ApiResponse.success("Booking completed successfully", response)
        );
    }

    // Mark as no-show
    @PutMapping("/{id}/no-show")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceBookingResponse>> markNoShow(@PathVariable Long id) {
        ResourceBookingResponse response = bookingService.markNoShow(id);
        return ResponseEntity.ok(
                ApiResponse.success("Booking marked as no-show", response)
        );
    }

    // Get upcoming bookings
    @GetMapping("/upcoming")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ResourceBookingResponse>>> getUpcomingBookings() {
        List<ResourceBookingResponse> bookings = bookingService.getUpcomingBookings();
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    // Get active bookings
    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ResourceBookingResponse>>> getActiveBookings() {
        List<ResourceBookingResponse> bookings = bookingService.getActiveBookings();
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }
}

package com.agrosolutions.AgroVia.service;
import com.agrosolutions.AgroVia.dto.AvailableSlotResponse;
import com.agrosolutions.AgroVia.dto.ResourceAvailabilityRequest;
import com.agrosolutions.AgroVia.dto.ResourceAvailabilityResponse;
import com.agrosolutions.AgroVia.entity.Resource;
import com.agrosolutions.AgroVia.entity.ResourceAvailability;
import com.agrosolutions.AgroVia.entity.User;
import com.agrosolutions.AgroVia.exception.BadRequestException;
import com.agrosolutions.AgroVia.exception.ResourceNotFoundException;
import com.agrosolutions.AgroVia.exception.UnauthorizedException;
import com.agrosolutions.AgroVia.repository.ResourceAvailabilityRepository;
import com.agrosolutions.AgroVia.repository.ResourceBookingRepository;
import com.agrosolutions.AgroVia.repository.ResourceRepository;
import com.agrosolutions.AgroVia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {
    @Autowired
    private ResourceAvailabilityRepository availabilityRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceBookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Get current logged in user
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Add recurring availability (weekly schedule)
    @Transactional
    public List<ResourceAvailabilityResponse> addRecurringAvailability(
            Long resourceId, List<ResourceAvailabilityRequest> requests) {

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        User currentUser = getCurrentUser();
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the owner can set availability");
        }

        List<ResourceAvailabilityResponse> responses = new ArrayList<>();

        for (ResourceAvailabilityRequest request : requests) {
            // Validate request
            if (request.getAvailabilityType() == null ||
                    !request.getAvailabilityType().equals("RECURRING")) {
                throw new BadRequestException("Invalid availability type for recurring schedule");
            }

            if (request.getDayOfWeek() == null) {
                throw new BadRequestException("Day of week is required for recurring availability");
            }

            if (request.getStartTime() == null || request.getEndTime() == null) {
                throw new BadRequestException("Start time and end time are required");
            }

            if (request.getStartTime().isAfter(request.getEndTime())) {
                throw new BadRequestException("Start time must be before end time");
            }

            // Check for overlapping recurring slots
            List<ResourceAvailability> existing = availabilityRepository
                    .findByResourceAndDayOfWeek(resource, request.getDayOfWeek());

            for (ResourceAvailability existingSlot : existing) {
                if (existingSlot.getAvailabilityType() == ResourceAvailability.AvailabilityType.RECURRING &&
                        timesOverlap(request.getStartTime(), request.getEndTime(),
                                existingSlot.getStartTime(), existingSlot.getEndTime())) {
                    throw new BadRequestException(
                            "New availability overlaps with existing slot on " + request.getDayOfWeek());
                }
            }

            // Create new availability slot
            ResourceAvailability availability = new ResourceAvailability(
                    resource,
                    request.getDayOfWeek(),
                    request.getStartTime(),
                    request.getEndTime()
            );
            availability.setAvailable(request.isAvailable());
            availability.setNotes(request.getNotes());

            ResourceAvailability saved = availabilityRepository.save(availability);
            responses.add(mapToAvailabilityResponse(saved));
        }

        return responses;
    }

    // Add custom date range availability
    @Transactional
    public List<ResourceAvailabilityResponse> addCustomAvailability(
            Long resourceId, List<ResourceAvailabilityRequest> requests) {

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        User currentUser = getCurrentUser();
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the owner can set availability");
        }

        List<ResourceAvailabilityResponse> responses = new ArrayList<>();

        for (ResourceAvailabilityRequest request : requests) {
            // Validate request
            if (request.getAvailabilityType() == null ||
                    !request.getAvailabilityType().equals("CUSTOM")) {
                throw new BadRequestException("Invalid availability type for custom range");
            }

            if (request.getStartDate() == null || request.getEndDate() == null) {
                throw new BadRequestException("Start date and end date are required for custom range");
            }

            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new BadRequestException("Start date must be before end date");
            }

            if (request.getStartDate().isBefore(LocalDate.now())) {
                throw new BadRequestException("Start date cannot be in the past");
            }

            if (request.getStartTime() == null || request.getEndTime() == null) {
                throw new BadRequestException("Start time and end time are required");
            }

            if (request.getStartTime().isAfter(request.getEndTime())) {
                throw new BadRequestException("Start time must be before end time");
            }

            // Check for overlapping custom slots in the date range
            List<ResourceAvailability> existing = availabilityRepository
                    .findCustomAvailabilityForDateRange(resource, request.getStartDate(), request.getEndDate());

            for (ResourceAvailability existingSlot : existing) {
                if (dateRangesOverlap(
                        request.getStartDate(), request.getEndDate(),
                        existingSlot.getStartDate(), existingSlot.getEndDate()) &&
                        timesOverlap(request.getStartTime(), request.getEndTime(),
                                existingSlot.getStartTime(), existingSlot.getEndTime())) {
                    throw new BadRequestException(
                            "New availability overlaps with existing slot in the date range");
                }
            }

            // Create new availability slot
            ResourceAvailability availability = new ResourceAvailability(
                    resource,
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getStartTime(),
                    request.getEndTime()
            );
            availability.setAvailable(request.isAvailable());
            availability.setNotes(request.getNotes());

            ResourceAvailability saved = availabilityRepository.save(availability);
            responses.add(mapToAvailabilityResponse(saved));
        }

        return responses;
    }

    // Get all availability for a resource
    public List<ResourceAvailabilityResponse> getResourceAvailability(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        return availabilityRepository.findByResourceOrderByCreatedAtAsc(resource)
                .stream()
                .map(this::mapToAvailabilityResponse)
                .collect(Collectors.toList());
    }

    // Get available time slots for a specific date range
    public List<AvailableSlotResponse> getAvailableSlots(
            Long resourceId, LocalDate startDate, LocalDate endDate) {

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        List<AvailableSlotResponse> availableSlots = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

            // Get recurring availability for this day of week
            List<ResourceAvailability> recurringSlots = availabilityRepository
                    .findByResourceAndDayOfWeek(resource, dayOfWeek);

            // Get custom availability covering this date
            List<ResourceAvailability> customSlots = availabilityRepository
                    .findCustomAvailabilityForDateRange(resource, currentDate, currentDate);

            // Combine and process all slots
            List<ResourceAvailability> allSlots = new ArrayList<>();
            allSlots.addAll(recurringSlots);
            allSlots.addAll(customSlots);

            for (ResourceAvailability slot : allSlots) {
                if (slot.isAvailable()) {
                    // Create time slots for this date (you might want to split into hourly slots)
                    LocalDateTime slotStart = LocalDateTime.of(currentDate, slot.getStartTime());
                    LocalDateTime slotEnd = LocalDateTime.of(currentDate, slot.getEndTime());

                    // Check if this slot is already booked
                    boolean isBooked = bookingRepository.hasConflictingBookings(
                            resource, slotStart, slotEnd);

                    if (!isBooked) {
                        availableSlots.add(new AvailableSlotResponse(slotStart, slotEnd, true));
                    }
                }
            }

            currentDate = currentDate.plusDays(1);
        }

        return availableSlots;
    }

    // Update availability slot
    @Transactional
    public ResourceAvailabilityResponse updateAvailability(
            Long availabilityId, ResourceAvailabilityRequest request) {

        ResourceAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found"));

        Resource resource = availability.getResource();
        User currentUser = getCurrentUser();

        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the owner can update availability");
        }

        // Update fields
        availability.setAvailable(request.isAvailable());
        availability.setNotes(request.getNotes());

        if (availability.getAvailabilityType() == ResourceAvailability.AvailabilityType.RECURRING) {
            if (request.getStartTime() != null) availability.setStartTime(request.getStartTime());
            if (request.getEndTime() != null) availability.setEndTime(request.getEndTime());
        } else {
            if (request.getStartDate() != null) availability.setStartDate(request.getStartDate());
            if (request.getEndDate() != null) availability.setEndDate(request.getEndDate());
            if (request.getStartTime() != null) availability.setStartTime(request.getStartTime());
            if (request.getEndTime() != null) availability.setEndTime(request.getEndTime());
        }

        ResourceAvailability updated = availabilityRepository.save(availability);
        return mapToAvailabilityResponse(updated);
    }

    // Delete availability slot
    @Transactional
    public void deleteAvailability(Long availabilityId) {
        ResourceAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found"));

        Resource resource = availability.getResource();
        User currentUser = getCurrentUser();

        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the owner can delete availability");
        }

        // Check if there are any upcoming bookings using this availability slot
        // This is a simplified check - you might want more sophisticated logic
        if (availability.getAvailabilityType() == ResourceAvailability.AvailabilityType.RECURRING) {
            // For recurring, you might want to check future dates
            LocalDateTime nextOccurrence = getNextOccurrence(availability);
            if (nextOccurrence != null) {
                boolean hasBookings = bookingRepository.hasConflictingBookings(
                        resource, nextOccurrence, nextOccurrence.plusHours(1));
                if (hasBookings) {
                    throw new BadRequestException(
                            "Cannot delete availability with upcoming bookings");
                }
            }
        }

        availabilityRepository.delete(availability);
    }

    // Check if resource is available for a specific datetime
    public boolean isResourceAvailable(Long resourceId, LocalDateTime dateTime) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        LocalDate date = dateTime.toLocalDate();
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        LocalTime time = dateTime.toLocalTime();

        return availabilityRepository.isResourceAvailableAt(resource, date, dayOfWeek, dateTime);
    }

    // Helper method to check if two time ranges overlap
    private boolean timesOverlap(LocalTime start1, LocalTime end1,
                                 LocalTime start2, LocalTime end2) {
        return !(end1.isBefore(start2) || end2.isBefore(start1));
    }

    // Helper method to check if two date ranges overlap
    private boolean dateRangesOverlap(LocalDate start1, LocalDate end1,
                                      LocalDate start2, LocalDate end2) {
        return !(end1.isBefore(start2) || end2.isBefore(start1));
    }

    // Helper method to get next occurrence of recurring availability
    private LocalDateTime getNextOccurrence(ResourceAvailability availability) {
        if (availability.getAvailabilityType() != ResourceAvailability.AvailabilityType.RECURRING) {
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalDate nextDate = today.plusDays(1);

        while (nextDate.isBefore(today.plusMonths(1))) {
            if (nextDate.getDayOfWeek() == availability.getDayOfWeek()) {
                return LocalDateTime.of(nextDate, availability.getStartTime());
            }
            nextDate = nextDate.plusDays(1);
        }

        return null;
    }

    // Map ResourceAvailability to ResourceAvailabilityResponse
    private ResourceAvailabilityResponse mapToAvailabilityResponse(ResourceAvailability availability) {
        ResourceAvailabilityResponse response = new ResourceAvailabilityResponse();
        response.setId(availability.getId());
        response.setAvailabilityType(availability.getAvailabilityType().toString());
        response.setDayOfWeek(availability.getDayOfWeek());
        response.setStartTime(availability.getStartTime());
        response.setEndTime(availability.getEndTime());
        response.setStartDate(availability.getStartDate());
        response.setEndDate(availability.getEndDate());
        response.setAvailable(availability.isAvailable());
        response.setNotes(availability.getNotes());
        return response;
    }
}

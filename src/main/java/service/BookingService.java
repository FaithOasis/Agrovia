package service;

import com.agrosolutions.AgroVia.dto.*;
import entity.*;
import com.agrosolutions.AgroVia.exception.BadRequestException;
import com.agrosolutions.AgroVia.exception.ResourceNotFoundException;
import com.agrosolutions.AgroVia.exception.UnauthorizedException;
import repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private ResourceBookingRepository bookingRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private NotificationService notificationService;

    // Get current logged in user
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Create new booking request
    @Transactional
    public ResourceBookingResponse createBooking(ResourceBookingRequest request) {
        User currentUser = getCurrentUser();
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        // Validate booking request
        validateBookingRequest(resource, currentUser, request);

        // Calculate total price
        BigDecimal totalPrice = calculateTotalPrice(resource, request);

        // Create booking
        ResourceBooking booking = new ResourceBooking();
        booking.setResource(resource);
        booking.setRenter(currentUser);
        booking.setStartDateTime(request.getStartDateTime());
        booking.setEndDateTime(request.getEndDateTime());
        booking.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);
        booking.setTotalPrice(totalPrice);
        booking.setDepositPaid(resource.getDepositAmount());
        booking.setPurpose(request.getPurpose());
        booking.setSpecialRequests(request.getSpecialRequests());
        booking.setStatus(ResourceBooking.BookingStatus.PENDING);

        ResourceBooking savedBooking = bookingRepository.save(booking);

        // Send notification to owner
        String message = String.format("New booking request for %s from %s",
                resource.getTitle(), currentUser.getUsername());
        notificationService.createSystemAnnouncement(message, resource.getOwner());

        return mapToBookingResponse(savedBooking);
    }

    // Validate booking request
    private void validateBookingRequest(Resource resource, User user,
                                        ResourceBookingRequest request) {
        // Check if user is not the owner
        if (resource.getOwner().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot book your own resource");
        }

        // Check if resource is available
        if (resource.getStatus() != Resource.ResourceStatus.AVAILABLE) {
            throw new BadRequestException("Resource is not available for booking");
        }

        // Check dates
        LocalDateTime now = LocalDateTime.now();
        if (request.getStartDateTime().isBefore(now)) {
            throw new BadRequestException("Start date cannot be in the past");
        }

        if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
            throw new BadRequestException("End date must be after start date");
        }

        // Check minimum rental period
        long hours = ChronoUnit.HOURS.between(request.getStartDateTime(), request.getEndDateTime());
        if (resource.getMinRentalPeriod() != null && hours < resource.getMinRentalPeriod()) {
            throw new BadRequestException(
                    "Minimum rental period is " + resource.getMinRentalPeriod() + " hours");
        }

        // Check maximum rental period
        if (resource.getMaxRentalPeriod() != null && hours > resource.getMaxRentalPeriod()) {
            throw new BadRequestException(
                    "Maximum rental period is " + resource.getMaxRentalPeriod() + " hours");
        }

        // Check advance notice
        if (resource.getAdvanceNotice() != null) {
            long hoursUntilStart = ChronoUnit.HOURS.between(now, request.getStartDateTime());
            if (hoursUntilStart < resource.getAdvanceNotice()) {
                throw new BadRequestException(
                        "Advance notice of " + resource.getAdvanceNotice() + " hours required");
            }
        }

        // Check availability
        if (!availabilityService.isResourceAvailable(resource.getId(), request.getStartDateTime()) ||
                !availabilityService.isResourceAvailable(resource.getId(), request.getEndDateTime())) {
            throw new BadRequestException("Resource is not available at the requested time");
        }

        // Check for conflicting bookings
        if (bookingRepository.hasConflictingBookings(resource,
                request.getStartDateTime(), request.getEndDateTime())) {
            throw new BadRequestException("Resource is already booked for this time period");
        }

        // Check quantity available
        if (request.getQuantity() != null &&
                request.getQuantity() > resource.getQuantityAvailable()) {
            throw new BadRequestException("Requested quantity exceeds available quantity");
        }
    }

    // Calculate total price
    private BigDecimal calculateTotalPrice(Resource resource, ResourceBookingRequest request) {
        long hours = ChronoUnit.HOURS.between(request.getStartDateTime(), request.getEndDateTime());
        long days = hours / 24;
        long weeks = days / 7;
        long remainingDays = days % 7;
        long remainingHours = hours % 24;

        BigDecimal total = BigDecimal.ZERO;

        // Calculate weekly price
        if (weeks > 0 && resource.getPricePerWeek() != null) {
            total = total.add(resource.getPricePerWeek().multiply(BigDecimal.valueOf(weeks)));
        }

        // Calculate daily price for remaining days
        if (remainingDays > 0 && resource.getPricePerDay() != null) {
            total = total.add(resource.getPricePerDay().multiply(BigDecimal.valueOf(remainingDays)));
        } else if (remainingDays > 0 && resource.getPricePerHour() != null) {
            // If no daily rate, use hourly rate
            total = total.add(resource.getPricePerHour()
                    .multiply(BigDecimal.valueOf(remainingDays * 24)));
        }

        // Calculate hourly price for remaining hours
        if (remainingHours > 0 && resource.getPricePerHour() != null) {
            total = total.add(resource.getPricePerHour().multiply(BigDecimal.valueOf(remainingHours)));
        }

        // Multiply by quantity
        if (request.getQuantity() != null && request.getQuantity() > 1) {
            total = total.multiply(BigDecimal.valueOf(request.getQuantity()));
        }

        return total;
    }

    // Confirm booking (owner action)
    @Transactional
    public ResourceBookingResponse confirmBooking(Long bookingId) {
        ResourceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User currentUser = getCurrentUser();
        Resource resource = booking.getResource();

        // Check if current user is the owner
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the owner can confirm bookings");
        }

        // Check if booking can be confirmed
        if (booking.getStatus() != ResourceBooking.BookingStatus.PENDING) {
            throw new BadRequestException("Only pending bookings can be confirmed");
        }

        // Double-check availability (in case someone else booked in the meantime)
        if (bookingRepository.hasConflictingBookings(resource,
                booking.getStartDateTime(), booking.getEndDateTime())) {
            booking.setStatus(ResourceBooking.BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            throw new BadRequestException("Resource is no longer available for this time period");
        }

        booking.setStatus(ResourceBooking.BookingStatus.CONFIRMED);
        booking.setConfirmedAt(LocalDateTime.now());
        booking.setConfirmedBy(currentUser.getId());

        ResourceBooking confirmedBooking = bookingRepository.save(booking);

        // Send notification to renter
        String message = String.format("Your booking for %s has been confirmed",
                resource.getTitle());
        notificationService.createSystemAnnouncement(message, booking.getRenter());

        return mapToBookingResponse(confirmedBooking);
    }

    // Cancel booking (renter or owner)
    @Transactional
    public ResourceBookingResponse cancelBooking(Long bookingId, String reason) {
        ResourceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User currentUser = getCurrentUser();
        Resource resource = booking.getResource();

        // Check if user is authorized to cancel
        boolean isRenter = booking.getRenter().getId().equals(currentUser.getId());
        boolean isOwner = resource.getOwner().getId().equals(currentUser.getId());

        if (!isRenter && !isOwner) {
            throw new UnauthorizedException("You are not authorized to cancel this booking");
        }

        // Check if booking can be cancelled
        if (booking.getStatus() == ResourceBooking.BookingStatus.COMPLETED ||
                booking.getStatus() == ResourceBooking.BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking cannot be cancelled");
        }

        // Apply cancellation logic based on timing
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilStart = ChronoUnit.HOURS.between(now, booking.getStartDateTime());

        // Simple cancellation policy: full refund if cancelled more than 24 hours before start
        if (hoursUntilStart >= 24) {
            // Full refund
            booking.setDepositPaid(BigDecimal.ZERO);
        } else if (hoursUntilStart >= 1) {
            // Partial refund (keep deposit)
            // Deposit is already set, keep it
        } else {
            // No refund, mark as no-show instead of cancel?
            // For now, allow cancellation but keep full payment
        }

        booking.setStatus(ResourceBooking.BookingStatus.CANCELLED);
        booking.setCancelledAt(now);
        booking.setCancelledBy(currentUser.getId());
        booking.setCancellationReason(reason);

        ResourceBooking cancelledBooking = bookingRepository.save(booking);

        // Notify the other party
        User notifyUser = isRenter ? resource.getOwner() : booking.getRenter();
        String message = String.format("Booking for %s has been cancelled by %s. Reason: %s",
                resource.getTitle(), currentUser.getUsername(),
                reason != null ? reason : "No reason provided");
        notificationService.createSystemAnnouncement(message, notifyUser);

        return mapToBookingResponse(cancelledBooking);
    }

    // Mark booking as active (when rental period starts)
    @Transactional
    public ResourceBookingResponse startBooking(Long bookingId) {
        ResourceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User currentUser = getCurrentUser();
        Resource resource = booking.getResource();

        // Only owner can mark as active
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the owner can start the booking");
        }

        LocalDateTime now = LocalDateTime.now();

        // Check if it's time to start
        if (now.isBefore(booking.getStartDateTime())) {
            throw new BadRequestException("Booking cannot start before the scheduled start time");
        }

        if (now.isAfter(booking.getEndDateTime())) {
            throw new BadRequestException("Booking period has already ended");
        }

        if (booking.getStatus() != ResourceBooking.BookingStatus.CONFIRMED) {
            throw new BadRequestException("Only confirmed bookings can be started");
        }

        booking.setStatus(ResourceBooking.BookingStatus.ACTIVE);
        ResourceBooking activeBooking = bookingRepository.save(booking);

        // Notify renter
        String message = String.format("Your booking for %s has started", resource.getTitle());
        notificationService.createSystemAnnouncement(message, booking.getRenter());

        return mapToBookingResponse(activeBooking);
    }

    // Mark booking as completed
    @Transactional
    public ResourceBookingResponse completeBooking(Long bookingId) {
        ResourceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User currentUser = getCurrentUser();
        Resource resource = booking.getResource();

        // Both owner and renter can mark as complete? Or just owner?
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the owner can complete the booking");
        }

        if (booking.getStatus() != ResourceBooking.BookingStatus.ACTIVE &&
                booking.getStatus() != ResourceBooking.BookingStatus.CONFIRMED) {
            throw new BadRequestException("Only active or confirmed bookings can be completed");
        }

        booking.setStatus(ResourceBooking.BookingStatus.COMPLETED);
        booking.setCompletedAt(LocalDateTime.now());

        ResourceBooking completedBooking = bookingRepository.save(booking);

        // Notify renter
        String message = String.format("Your booking for %s has been completed. Please leave a review!",
                resource.getTitle());
        notificationService.createSystemAnnouncement(message, booking.getRenter());

        return mapToBookingResponse(completedBooking);
    }

    // Mark booking as no-show
    @Transactional
    public ResourceBookingResponse markNoShow(Long bookingId) {
        ResourceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User currentUser = getCurrentUser();
        Resource resource = booking.getResource();

        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the owner can mark no-show");
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(booking.getStartDateTime())) {
            throw new BadRequestException("Cannot mark no-show before the start time");
        }

        if (booking.getStatus() != ResourceBooking.BookingStatus.CONFIRMED) {
            throw new BadRequestException("Only confirmed bookings can be marked as no-show");
        }

        booking.setStatus(ResourceBooking.BookingStatus.NO_SHOW);
        booking.setNoShowAt(now);

        ResourceBooking noShowBooking = bookingRepository.save(booking);

        // Notify renter
        String message = String.format("You were marked as no-show for %s", resource.getTitle());
        notificationService.createSystemAnnouncement(message, booking.getRenter());

        return mapToBookingResponse(noShowBooking);
    }

    // Get my bookings (as renter)
    public Page<ResourceBookingResponse> getMyBookings(Pageable pageable) {
        User currentUser = getCurrentUser();
        return bookingRepository.findByRenter(currentUser, pageable)
                .map(this::mapToBookingResponse);
    }

    // Get bookings for my resources (as owner)
    public Page<ResourceBookingResponse> getReceivedBookings(Pageable pageable) {
        User currentUser = getCurrentUser();
        return bookingRepository.findByResourceOwner(currentUser, pageable)
                .map(this::mapToBookingResponse);
    }

    // Get booking by ID
    public ResourceBookingResponse getBookingById(Long id) {
        ResourceBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User currentUser = getCurrentUser();
        Resource resource = booking.getResource();

        // Check if user is renter or owner
        if (!booking.getRenter().getId().equals(currentUser.getId()) &&
                !resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to view this booking");
        }

        return mapToBookingResponse(booking);
    }

    // Get upcoming bookings
    public List<ResourceBookingResponse> getUpcomingBookings() {
        User currentUser = getCurrentUser();
        List<ResourceBooking> bookings = bookingRepository.findUpcomingBookingsForRenter(currentUser);
        bookings.addAll(bookingRepository.findUpcomingBookingsForOwner(currentUser));

        return bookings.stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    // Get active bookings
    public List<ResourceBookingResponse> getActiveBookings() {
        return bookingRepository.findActiveBookings()
                .stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    // Check if user can review this booking
    private boolean canReview(ResourceBooking booking, User user) {
        return booking.getStatus() == ResourceBooking.BookingStatus.COMPLETED &&
                booking.getRenter().getId().equals(user.getId()) &&
                booking.getReview() == null;
    }

    // Map ResourceBooking to ResourceBookingResponse
    private ResourceBookingResponse mapToBookingResponse(ResourceBooking booking) {
        ResourceBookingResponse response = new ResourceBookingResponse();
        response.setId(booking.getId());
        response.setBookingNumber(booking.getBookingNumber());
        response.setStartDateTime(booking.getStartDateTime());
        response.setEndDateTime(booking.getEndDateTime());
        response.setQuantity(booking.getQuantity());
        response.setTotalPrice(booking.getTotalPrice());
        response.setDepositPaid(booking.getDepositPaid());
        response.setStatus(booking.getStatus().toString());
        response.setPurpose(booking.getPurpose());
        response.setSpecialRequests(booking.getSpecialRequests());
        response.setCreatedAt(booking.getCreatedAt());
        response.setConfirmedAt(booking.getConfirmedAt());
        response.setCompletedAt(booking.getCompletedAt());
        response.setCancelledAt(booking.getCancelledAt());

        // Map resource summary
        if (booking.getResource() != null) {
            Resource resource = booking.getResource();
            UserSummary ownerSummary = new UserSummary(
                    resource.getOwner().getId(),
                    resource.getOwner().getUsername(),
                    resource.getOwner().getEmail(),
                    resource.getOwner().getFullName()
            );

            CategorySummary categorySummary = null;
            if (resource.getCategory() != null) {
                categorySummary = new CategorySummary(
                        resource.getCategory().getId(),
                        resource.getCategory().getName()
                );
            }

            String primaryImageUrl = null;
            if (resource.getImages() != null && !resource.getImages().isEmpty()) {
                ResourceImage primary = resource.getImages().stream()
                        .filter(ResourceImage::isPrimary)
                        .findFirst()
                        .orElse(resource.getImages().get(0));
                primaryImageUrl = primary.getImageUrl();
            }

            ResourceSummary resourceSummary = new ResourceSummary(
                    resource.getId(),
                    resource.getTitle(),
                    ownerSummary,
                    categorySummary,
                    resource.getPricePerDay(),
                    resource.getLocation(),
                    resource.getStatus().toString(),
                    primaryImageUrl,
                    resource.getAverageRating(),
                    resource.getReviewCount()
            );
            response.setResource(resourceSummary);
        }

        // Map renter
        if (booking.getRenter() != null) {
            UserSummary renterSummary = new UserSummary(
                    booking.getRenter().getId(),
                    booking.getRenter().getUsername(),
                    booking.getRenter().getEmail(),
                    booking.getRenter().getFullName()
            );
            response.setRenter(renterSummary);
        }

        // Check if can review
        User currentUser = null;
        try {
            currentUser = getCurrentUser();
            response.setCanReview(canReview(booking, currentUser));
        } catch (Exception e) {
            response.setCanReview(false);
        }

        response.setHasReview(booking.getReview() != null);

        return response;
    }
}
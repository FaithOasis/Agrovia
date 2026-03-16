package com.agrosolutions.AgroVia.service;
import com.agrosolutions.AgroVia.dto.OwnerResponseRequest;
import com.agrosolutions.AgroVia.dto.ResourceReviewRequest;
import com.agrosolutions.AgroVia.dto.ResourceReviewResponse;
import com.agrosolutions.AgroVia.dto.UserSummary;
import com.agrosolutions.AgroVia.dto.CategorySummary;
import com.agrosolutions.AgroVia.dto.ResourceSummary;
import com.agrosolutions.AgroVia.entity.Resource;
import com.agrosolutions.AgroVia.entity.ResourceBooking;
import com.agrosolutions.AgroVia.entity.ResourceImage;
import com.agrosolutions.AgroVia.entity.ResourceReview;
import com.agrosolutions.AgroVia.entity.User;
import com.agrosolutions.AgroVia.exception.BadRequestException;
import com.agrosolutions.AgroVia.exception.ResourceNotFoundException;
import com.agrosolutions.AgroVia.exception.UnauthorizedException;
import com.agrosolutions.AgroVia.repository.ResourceBookingRepository;
import com.agrosolutions.AgroVia.repository.ResourceRepository;
import com.agrosolutions.AgroVia.repository.ResourceReviewRepository;
import com.agrosolutions.AgroVia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    @Autowired
    private ResourceReviewRepository reviewRepository;

    @Autowired
    private ResourceBookingRepository bookingRepository;

    @Autowired
    private ResourceRepository resourceRepository;

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

    // Submit a review
    @Transactional
    public ResourceReviewResponse createReview(ResourceReviewRequest request) {
        User currentUser = getCurrentUser();

        ResourceBooking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Validate review request
        validateReviewRequest(booking, currentUser, request);

        // Create review
        ResourceReview review = new ResourceReview(
                booking,
                booking.getResource(),
                currentUser,
                request.getRating(),
                request.getComment()
        );

        ResourceReview savedReview = reviewRepository.save(review);

        // Notify owner
        String message = String.format("New review received for %s: %d/5 stars",
                booking.getResource().getTitle(), request.getRating());
        notificationService.createSystemAnnouncement(message, booking.getResource().getOwner());

        return mapToReviewResponse(savedReview);
    }

    // Validate review request
    private void validateReviewRequest(ResourceBooking booking, User user,
                                       ResourceReviewRequest request) {
        // Check if user is the renter
        if (!booking.getRenter().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only the renter can review this booking");
        }

        // Check if booking is completed
        if (booking.getStatus() != ResourceBooking.BookingStatus.COMPLETED) {
            throw new BadRequestException("You can only review completed bookings");
        }

        // Check if review already exists
        if (reviewRepository.findByBooking(booking).isPresent()) {
            throw new BadRequestException("You have already reviewed this booking");
        }

        // Validate rating
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        // Validate comment
        if (request.getComment() == null || request.getComment().trim().isEmpty()) {
            throw new BadRequestException("Comment is required");
        }

        if (request.getComment().length() > 1000) {
            throw new BadRequestException("Comment cannot exceed 1000 characters");
        }
    }

    // Update review
    @Transactional
    public ResourceReviewResponse updateReview(Long reviewId, ResourceReviewRequest request) {
        ResourceReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        User currentUser = getCurrentUser();

        // Check if user is the reviewer
        if (!review.getReviewer().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only update your own reviews");
        }

        // Validate rating
        if (request.getRating() != null) {
            if (request.getRating() < 1 || request.getRating() > 5) {
                throw new BadRequestException("Rating must be between 1 and 5");
            }
            review.setRating(request.getRating());
        }

        // Update comment
        if (request.getComment() != null && !request.getComment().trim().isEmpty()) {
            if (request.getComment().length() > 1000) {
                throw new BadRequestException("Comment cannot exceed 1000 characters");
            }
            review.setComment(request.getComment());
        }

        review.setUpdatedAt(LocalDateTime.now());
        ResourceReview updatedReview = reviewRepository.save(review);

        return mapToReviewResponse(updatedReview);
    }

    // Delete review
    @Transactional
    public void deleteReview(Long reviewId) {
        ResourceReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        User currentUser = getCurrentUser();
        Resource resource = review.getResource();

        // Allow deletion by reviewer or resource owner
        boolean isReviewer = review.getReviewer().getId().equals(currentUser.getId());
        boolean isOwner = resource.getOwner().getId().equals(currentUser.getId());

        if (!isReviewer && !isOwner) {
            throw new UnauthorizedException("You are not authorized to delete this review");
        }

        reviewRepository.delete(review);
    }

    // Get reviews for a resource
    public Page<ResourceReviewResponse> getResourceReviews(Long resourceId, Pageable pageable) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        return reviewRepository.findByResourceOrderByCreatedAtDesc(resource, pageable)
                .map(this::mapToReviewResponse);
    }

    // Get reviews by a user
    public Page<ResourceReviewResponse> getUserReviews(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return reviewRepository.findByReviewer(user, pageable)
                .map(this::mapToReviewResponse);
    }

    // Get my reviews
    public Page<ResourceReviewResponse> getMyReviews(Pageable pageable) {
        User currentUser = getCurrentUser();
        return reviewRepository.findByReviewer(currentUser, pageable)
                .map(this::mapToReviewResponse);
    }

    // Get reviews for my resources (as owner)
    public Page<ResourceReviewResponse> getReviewsForMyResources(Pageable pageable) {
        User currentUser = getCurrentUser();
        return reviewRepository.findReviewsForOwnerResources(currentUser, pageable)
                .map(this::mapToReviewResponse);
    }

    // Owner responds to review
    @Transactional
    public ResourceReviewResponse respondToReview(Long reviewId, OwnerResponseRequest request) {
        ResourceReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        User currentUser = getCurrentUser();
        Resource resource = review.getResource();

        // Check if user is the resource owner
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the resource owner can respond to reviews");
        }

        // Check if already responded
        if (review.getOwnerResponse() != null && !review.getOwnerResponse().isEmpty()) {
            throw new BadRequestException("You have already responded to this review");
        }

        // Validate response
        if (request.getResponse() == null || request.getResponse().trim().isEmpty()) {
            throw new BadRequestException("Response cannot be empty");
        }

        if (request.getResponse().length() > 1000) {
            throw new BadRequestException("Response cannot exceed 1000 characters");
        }

        review.setOwnerResponse(request.getResponse());
        review.setResponseCreatedAt(LocalDateTime.now());

        ResourceReview updatedReview = reviewRepository.save(review);

        // Notify reviewer
        String message = String.format("The owner of %s has responded to your review",
                resource.getTitle());
        notificationService.createSystemAnnouncement(message, review.getReviewer());

        return mapToReviewResponse(updatedReview);
    }

    // Update owner response
    @Transactional
    public ResourceReviewResponse updateResponse(Long reviewId, OwnerResponseRequest request) {
        ResourceReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        User currentUser = getCurrentUser();
        Resource resource = review.getResource();

        // Check if user is the resource owner
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the resource owner can update responses");
        }

        // Check if response exists
        if (review.getOwnerResponse() == null) {
            throw new BadRequestException("No response exists to update");
        }

        // Validate response
        if (request.getResponse() == null || request.getResponse().trim().isEmpty()) {
            throw new BadRequestException("Response cannot be empty");
        }

        if (request.getResponse().length() > 1000) {
            throw new BadRequestException("Response cannot exceed 1000 characters");
        }

        review.setOwnerResponse(request.getResponse());
        review.setResponseCreatedAt(LocalDateTime.now());

        ResourceReview updatedReview = reviewRepository.save(review);

        return mapToReviewResponse(updatedReview);
    }

    // Delete owner response
    @Transactional
    public void deleteResponse(Long reviewId) {
        ResourceReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        User currentUser = getCurrentUser();
        Resource resource = review.getResource();

        // Check if user is the resource owner
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the resource owner can delete responses");
        }

        review.setOwnerResponse(null);
        review.setResponseCreatedAt(null);

        reviewRepository.save(review);
    }

    // Get average rating for a resource
    public Double getAverageRating(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        return reviewRepository.getAverageRatingForResource(resource);
    }

    // Get rating distribution for a resource
    public List<RatingDistribution> getRatingDistribution(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        List<Object[]> distribution = reviewRepository.getRatingDistribution(resource);

        return distribution.stream()
                .map(obj -> new RatingDistribution((Integer) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
    }

    // Get top rated resources
    public List<TopRatedResource> getTopRatedResources(int limit, int minReviews) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = reviewRepository.findTopRatedResources(minReviews, pageable);

        return results.stream()
                .map(obj -> {
                    Resource resource = (Resource) obj[0];
                    Double avgRating = (Double) obj[1];
                    Long reviewCount = (Long) obj[2];

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
                            avgRating,
                            reviewCount.intValue()
                    );

                    return new TopRatedResource(resourceSummary, avgRating, reviewCount);
                })
                .collect(Collectors.toList());
    }

    // Map ResourceReview to ResourceReviewResponse
    private ResourceReviewResponse mapToReviewResponse(ResourceReview review) {
        ResourceReviewResponse response = new ResourceReviewResponse();
        response.setId(review.getId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setOwnerResponse(review.getOwnerResponse());
        response.setResponseCreatedAt(review.getResponseCreatedAt());
        response.setCreatedAt(review.getCreatedAt());

        // Map reviewer
        if (review.getReviewer() != null) {
            UserSummary reviewerSummary = new UserSummary(
                    review.getReviewer().getId(),
                    review.getReviewer().getUsername(),
                    review.getReviewer().getEmail(),
                    review.getReviewer().getFullName()
            );
            response.setReviewer(reviewerSummary);
        }

        return response;
    }

    // Inner class for rating distribution
    public static class RatingDistribution {
        private Integer rating;
        private Long count;

        public RatingDistribution(Integer rating, Long count) {
            this.rating = rating;
            this.count = count;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    // Inner class for top rated resources
    public static class TopRatedResource {
        private ResourceSummary resource;
        private Double averageRating;
        private Long reviewCount;

        public TopRatedResource(ResourceSummary resource, Double averageRating, Long reviewCount) {
            this.resource = resource;
            this.averageRating = averageRating;
            this.reviewCount = reviewCount;
        }

        public ResourceSummary getResource() {
            return resource;
        }

        public void setResource(ResourceSummary resource) {
            this.resource = resource;
        }

        public Double getAverageRating() {
            return averageRating;
        }

        public void setAverageRating(Double averageRating) {
            this.averageRating = averageRating;
        }

        public Long getReviewCount() {
            return reviewCount;
        }

        public void setReviewCount(Long reviewCount) {
            this.reviewCount = reviewCount;
        }
    }
}

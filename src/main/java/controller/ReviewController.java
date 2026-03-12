package controller;

import com.agrosolutions.AgroVia.dto.*;
import service.ReviewService;
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
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Submit a review
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceReviewResponse>> createReview(
            @Valid @RequestBody ResourceReviewRequest request) {
        ResourceReviewResponse response = reviewService.createReview(request);
        return new ResponseEntity<>(
                ApiResponse.success("Review submitted successfully", response),
                HttpStatus.CREATED
        );
    }

    // Update review
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceReviewResponse>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ResourceReviewRequest request) {
        ResourceReviewResponse response = reviewService.updateReview(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Review updated successfully", response)
        );
    }

    // Delete review
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(
                ApiResponse.success("Review deleted successfully", null)
        );
    }

    // Get reviews for a resource (public)
    @GetMapping("/resource/{resourceId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<Page<ResourceReviewResponse>>> getResourceReviews(
            @PathVariable Long resourceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ResourceReviewResponse> reviews = reviewService.getResourceReviews(resourceId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    // Get reviews by a user (public)
    @GetMapping("/user/{userId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<Page<ResourceReviewResponse>>> getUserReviews(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ResourceReviewResponse> reviews = reviewService.getUserReviews(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    // Get my reviews
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ResourceReviewResponse>>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ResourceReviewResponse> reviews = reviewService.getMyReviews(pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    // Get reviews for my resources (as owner)
    @GetMapping("/my-resources")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ResourceReviewResponse>>> getReviewsForMyResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ResourceReviewResponse> reviews = reviewService.getReviewsForMyResources(pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    // Owner responds to review
    @PostMapping("/{id}/respond")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceReviewResponse>> respondToReview(
            @PathVariable Long id,
            @Valid @RequestBody OwnerResponseRequest request) {
        ResourceReviewResponse response = reviewService.respondToReview(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Response submitted successfully", response)
        );
    }

    // Update owner response
    @PutMapping("/{id}/response")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceReviewResponse>> updateResponse(
            @PathVariable Long id,
            @Valid @RequestBody OwnerResponseRequest request) {
        ResourceReviewResponse response = reviewService.updateResponse(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Response updated successfully", response)
        );
    }

    // Delete owner response
    @DeleteMapping("/{id}/response")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteResponse(@PathVariable Long id) {
        reviewService.deleteResponse(id);
        return ResponseEntity.ok(
                ApiResponse.success("Response deleted successfully", null)
        );
    }

    // Get average rating for a resource (public)
    @GetMapping("/resource/{resourceId}/average")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(@PathVariable Long resourceId) {
        Double averageRating = reviewService.getAverageRating(resourceId);
        return ResponseEntity.ok(ApiResponse.success(averageRating));
    }

    // Get rating distribution for a resource (public)
    @GetMapping("/resource/{resourceId}/distribution")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<ReviewService.RatingDistribution>>> getRatingDistribution(
            @PathVariable Long resourceId) {
        List<ReviewService.RatingDistribution> distribution =
                reviewService.getRatingDistribution(resourceId);
        return ResponseEntity.ok(ApiResponse.success(distribution));
    }

    // Get top rated resources (public)
    @GetMapping("/top-rated")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<ReviewService.TopRatedResource>>> getTopRatedResources(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "1") int minReviews) {
        List<ReviewService.TopRatedResource> topRated =
                reviewService.getTopRatedResources(limit, minReviews);
        return ResponseEntity.ok(ApiResponse.success(topRated));
    }
}

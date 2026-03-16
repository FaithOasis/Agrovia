package com.agrosolutions.AgroVia.repository;
import com.agrosolutions.AgroVia.entity.Resource;
import com.agrosolutions.AgroVia.entity.ResourceBooking;
import com.agrosolutions.AgroVia.entity.ResourceReview;
import com.agrosolutions.AgroVia.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceReviewRepository extends JpaRepository<ResourceReview, Long> {

    // Find review by booking (one-to-one)
    Optional<ResourceReview> findByBooking(ResourceBooking booking);

    // Find all reviews for a resource
    Page<ResourceReview> findByResourceOrderByCreatedAtDesc(Resource resource, Pageable pageable);

    // Find all reviews by a reviewer
    Page<ResourceReview> findByReviewer(User reviewer, Pageable pageable);

    // Find reviews with owner responses
    List<ResourceReview> findByOwnerResponseIsNotNull();

    // Find reviews without owner responses
    List<ResourceReview> findByOwnerResponseIsNull();

    // Calculate average rating for a resource
    @Query("SELECT AVG(r.rating) FROM ResourceReview r WHERE r.resource = :resource")
    Double getAverageRatingForResource(@Param("resource") Resource resource);

    // Get rating distribution for a resource
    @Query("SELECT r.rating, COUNT(r) FROM ResourceReview r " +
            "WHERE r.resource = :resource GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistribution(@Param("resource") Resource resource);

    // Count reviews by rating
    long countByResourceAndRating(Resource resource, Integer rating);

    // Check if user has already reviewed a booking
    boolean existsByBooking(ResourceBooking booking);

    // Find recent reviews
    Page<ResourceReview> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Find reviews for resources owned by a user
    @Query("SELECT r FROM ResourceReview r WHERE r.resource.owner = :owner " +
            "ORDER BY r.createdAt DESC")
    Page<ResourceReview> findReviewsForOwnerResources(@Param("owner") User owner, Pageable pageable);

    // Get top-rated resources
    @Query("SELECT r.resource, AVG(r.rating) as avgRating, COUNT(r) as reviewCount " +
            "FROM ResourceReview r GROUP BY r.resource " +
            "HAVING COUNT(r) >= :minReviews ORDER BY avgRating DESC")
    List<Object[]> findTopRatedResources(@Param("minReviews") int minReviews, Pageable pageable);
}

package com.agrosolutions.AgroVia.repository;
import com.agrosolutions.AgroVia.entity.Resource;
import com.agrosolutions.AgroVia.entity.ResourceCategory;
import com.agrosolutions.AgroVia.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    // Find resources by owner
    Page<Resource> findByOwner(User owner, Pageable pageable);

    // Find resources by category
    Page<Resource> findByCategory(ResourceCategory category, Pageable pageable);

    // Find resources by status
    Page<Resource> findByStatus(Resource.ResourceStatus status, Pageable pageable);

    // Find resources by owner and status
    List<Resource> findByOwnerAndStatus(User owner, Resource.ResourceStatus status);

    // Search resources with multiple filters
    @Query("SELECT DISTINCT r FROM Resource r " +
            "LEFT JOIN FETCH r.category " +
            "LEFT JOIN FETCH r.images i " +
            "WHERE (:categoryId IS NULL OR r.category.id = :categoryId) " +
            "AND (:minPrice IS NULL OR r.pricePerDay >= :minPrice) " +
            "AND (:maxPrice IS NULL OR r.pricePerDay <= :maxPrice) " +
            "AND (:status IS NULL OR r.status = :status) " +
            "AND (:location IS NULL OR LOWER(r.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:searchTerm IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Resource> searchResources(@Param("categoryId") Long categoryId,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("status") Resource.ResourceStatus status,
                                   @Param("location") String location,
                                   @Param("searchTerm") String searchTerm,
                                   Pageable pageable);

    // Find resources within a radius (using Haversine formula)
    @Query(value = "SELECT r FROM Resource r WHERE " +
            "r.status = 'AVAILABLE' AND " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(r.latitude)) * " +
            "cos(radians(r.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(r.latitude)))) <= :radius")
    List<Resource> findResourcesWithinRadius(@Param("lat") Double latitude,
                                             @Param("lng") Double longitude,
                                             @Param("radius") Integer radius);

    // Find resources available for a specific date range
    @Query("SELECT r FROM Resource r WHERE r.status = 'AVAILABLE' AND r.id NOT IN " +
            "(SELECT b.resource.id FROM ResourceBooking b WHERE " +
            "b.status IN ('CONFIRMED', 'ACTIVE', 'PENDING') AND " +
            "((b.startDateTime BETWEEN :start AND :end) OR " +
            "(b.endDateTime BETWEEN :start AND :end) OR " +
            "(b.startDateTime <= :start AND b.endDateTime >= :end)))")
    List<Resource> findAvailableResourcesForDateRange(@Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end);

    // Find popular resources (most booked)
    @Query("SELECT r FROM Resource r ORDER BY r.bookingsCount DESC")
    Page<Resource> findPopularResources(Pageable pageable);

    // Find recently added resources
    Page<Resource> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Find resources by owner with booking count
    @Query("SELECT r, COUNT(b) FROM Resource r LEFT JOIN r.bookings b WHERE r.owner = :owner GROUP BY r")
    List<Object[]> findResourcesByOwnerWithBookingCount(@Param("owner") User owner);

    // Increment views count
    @Query("UPDATE Resource r SET r.viewsCount = r.viewsCount + 1 WHERE r.id = :id")
    void incrementViewsCount(@Param("id") Long id);

}

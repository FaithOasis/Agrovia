package repository;

import entity.Resource;
import entity.ResourceBooking;
import entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceBookingRepository extends JpaRepository<ResourceBooking, Long> {

    // Find booking by booking number
    Optional<ResourceBooking> findByBookingNumber(String bookingNumber);

    // Find bookings by renter
    Page<ResourceBooking> findByRenter(User renter, Pageable pageable);

    // Find bookings by resource owner (through resource)
    @Query("SELECT b FROM ResourceBooking b WHERE b.resource.owner = :owner")
    Page<ResourceBooking> findByResourceOwner(@Param("owner") User owner, Pageable pageable);

    // Find bookings by resource
    Page<ResourceBooking> findByResource(Resource resource, Pageable pageable);

    // Find bookings by status
    List<ResourceBooking> findByStatus(ResourceBooking.BookingStatus status);

    // Find bookings by resource and status
    List<ResourceBooking> findByResourceAndStatus(Resource resource, ResourceBooking.BookingStatus status);

    // Find conflicting bookings for a date range
    @Query("SELECT b FROM ResourceBooking b WHERE b.resource = :resource " +
            "AND b.status IN ('PENDING', 'CONFIRMED', 'ACTIVE') " +
            "AND ((b.startDateTime BETWEEN :start AND :end) OR " +
            "(b.endDateTime BETWEEN :start AND :end) OR " +
            "(b.startDateTime <= :start AND b.endDateTime >= :end))")
    List<ResourceBooking> findConflictingBookings(@Param("resource") Resource resource,
                                                  @Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    // Check if there are any conflicting bookings
    @Query("SELECT COUNT(b) > 0 FROM ResourceBooking b WHERE b.resource = :resource " +
            "AND b.status IN ('PENDING', 'CONFIRMED', 'ACTIVE') " +
            "AND ((b.startDateTime BETWEEN :start AND :end) OR " +
            "(b.endDateTime BETWEEN :start AND :end) OR " +
            "(b.startDateTime <= :start AND b.endDateTime >= :end))")
    boolean hasConflictingBookings(@Param("resource") Resource resource,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    // Find upcoming bookings for a user (as renter)
    @Query("SELECT b FROM ResourceBooking b WHERE b.renter = :user " +
            "AND b.status IN ('CONFIRMED', 'ACTIVE') " +
            "AND b.startDateTime > CURRENT_TIMESTAMP " +
            "ORDER BY b.startDateTime ASC")
    List<ResourceBooking> findUpcomingBookingsForRenter(@Param("user") User user);

    // Find upcoming bookings for a user (as owner)
    @Query("SELECT b FROM ResourceBooking b WHERE b.resource.owner = :user " +
            "AND b.status IN ('CONFIRMED', 'ACTIVE') " +
            "AND b.startDateTime > CURRENT_TIMESTAMP " +
            "ORDER BY b.startDateTime ASC")
    List<ResourceBooking> findUpcomingBookingsForOwner(@Param("user") User user);

    // Find active bookings (currently in progress)
    @Query("SELECT b FROM ResourceBooking b WHERE b.status = 'CONFIRMED' " +
            "AND b.startDateTime <= CURRENT_TIMESTAMP " +
            "AND b.endDateTime >= CURRENT_TIMESTAMP")
    List<ResourceBooking> findActiveBookings();

    // Find bookings that should be marked as no-show (past start time and not confirmed)
    @Query("SELECT b FROM ResourceBooking b WHERE b.status = 'PENDING' " +
            "AND b.startDateTime < CURRENT_TIMESTAMP")
    List<ResourceBooking> findOverduePendingBookings();

    // Find bookings that should be marked as completed (past end time and not completed)
    @Query("SELECT b FROM ResourceBooking b WHERE b.status = 'ACTIVE' " +
            "AND b.endDateTime < CURRENT_TIMESTAMP")
    List<ResourceBooking> findBookingsToComplete();

    // Count bookings by status for a resource
    @Query("SELECT COUNT(b) FROM ResourceBooking b WHERE b.resource = :resource AND b.status = :status")
    long countByResourceAndStatus(@Param("resource") Resource resource,
                                  @Param("status") ResourceBooking.BookingStatus status);

    // Get booking statistics for a resource
    @Query("SELECT MONTH(b.createdAt), COUNT(b), SUM(b.totalPrice) " +
            "FROM ResourceBooking b WHERE b.resource.owner = :owner " +
            "AND b.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY MONTH(b.createdAt)")
    List<Object[]> getBookingStatistics(@Param("owner") User owner,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
}

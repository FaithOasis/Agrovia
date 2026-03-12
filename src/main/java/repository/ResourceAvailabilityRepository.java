package repository;

import entity.Resource;
import entity.ResourceAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ResourceAvailabilityRepository extends JpaRepository<ResourceAvailability, Long> {

    // Find all availability for a resource
    List<ResourceAvailability> findByResourceOrderByCreatedAtAsc(Resource resource);

    // Find recurring availability for a resource
    List<ResourceAvailability> findByResourceAndAvailabilityType(
            Resource resource, ResourceAvailability.AvailabilityType type);

    // Find availability for a specific day of week
    List<ResourceAvailability> findByResourceAndDayOfWeek(Resource resource, DayOfWeek dayOfWeek);

    // Find custom availability for a date range
    @Query("SELECT a FROM ResourceAvailability a WHERE a.resource = :resource " +
            "AND a.availabilityType = 'CUSTOM' " +
            "AND ((a.startDate BETWEEN :startDate AND :endDate) OR " +
            "(a.endDate BETWEEN :startDate AND :endDate) OR " +
            "(a.startDate <= :startDate AND a.endDate >= :endDate))")
    List<ResourceAvailability> findCustomAvailabilityForDateRange(
            @Param("resource") Resource resource,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Check if resource is available at a specific datetime
    @Query("SELECT COUNT(a) > 0 FROM ResourceAvailability a WHERE a.resource = :resource " +
            "AND a.isAvailable = true AND " +
            "((a.availabilityType = 'RECURRING' AND a.dayOfWeek = :dayOfWeek " +
            "AND :time BETWEEN a.startTime AND a.endTime) OR " +
            "(a.availabilityType = 'CUSTOM' AND :date BETWEEN a.startDate AND a.endDate " +
            "AND :time BETWEEN a.startTime AND a.endTime))")
    boolean isResourceAvailableAt(@Param("resource") Resource resource,
                                  @Param("date") LocalDate date,
                                  @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                  @Param("time") LocalDateTime time);

    // Delete all availability for a resource
    void deleteByResource(Resource resource);

    // Find availability slots that are currently active
    @Query("SELECT a FROM ResourceAvailability a WHERE a.resource = :resource " +
            "AND a.isAvailable = true AND " +
            "((a.availabilityType = 'RECURRING' AND a.dayOfWeek = :dayOfWeek " +
            "AND CURRENT_TIME BETWEEN a.startTime AND a.endTime) OR " +
            "(a.availabilityType = 'CUSTOM' AND CURRENT_DATE BETWEEN a.startDate AND a.endDate " +
            "AND CURRENT_TIME BETWEEN a.startTime AND a.endTime))")
    List<ResourceAvailability> findCurrentAvailabilitySlots(
            @Param("resource") Resource resource,
            @Param("dayOfWeek") DayOfWeek dayOfWeek);
}
package repository;

import entity.Acquisition;
import entity.PaymentMilestone;
import org.springframework.data.domain.Pageable;  // ADD THIS IMPORT
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;  // ADD THIS IMPORT
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMilestoneRepository extends JpaRepository<PaymentMilestone, Long> {

    // Find all milestones for an acquisition
    List<PaymentMilestone> findByAcquisitionOrderByMilestoneNumberAsc(Acquisition acquisition);

    // Find milestone by number
    Optional<PaymentMilestone> findByAcquisitionAndMilestoneNumber(Acquisition acquisition,
                                                                   Integer milestoneNumber);

    // Find upcoming milestones
    @Query("SELECT m FROM PaymentMilestone m WHERE m.acquisition = :acquisition " +
            "AND m.status = 'PENDING' ORDER BY m.dueDate ASC")
    List<PaymentMilestone> findUpcomingMilestones(@Param("acquisition") Acquisition acquisition);

    // Find overdue milestones
    @Query("SELECT m FROM PaymentMilestone m WHERE m.status = 'PENDING' " +
            "AND m.dueDate < CURRENT_TIMESTAMP")
    List<PaymentMilestone> findOverdueMilestones();

    // Find completed milestones
    List<PaymentMilestone> findByAcquisitionAndStatus(Acquisition acquisition,
                                                      PaymentMilestone.MilestoneStatus status);

    // Get total amount due for an acquisition
    @Query("SELECT COALESCE(SUM(m.amountDue), 0) FROM PaymentMilestone m " +
            "WHERE m.acquisition = :acquisition")
    BigDecimal getTotalAmountDue(@Param("acquisition") Acquisition acquisition);

    // Get total amount paid for an acquisition
    @Query("SELECT COALESCE(SUM(m.amountPaid), 0) FROM PaymentMilestone m " +
            "WHERE m.acquisition = :acquisition")
    BigDecimal getTotalAmountPaid(@Param("acquisition") Acquisition acquisition);

    // Get milestone completion percentage
    @Query("SELECT COUNT(m) FROM PaymentMilestone m WHERE m.acquisition = :acquisition " +
            "AND m.status = 'COMPLETED'")
    int countCompletedMilestones(@Param("acquisition") Acquisition acquisition);

    // Find milestones due between dates
    List<PaymentMilestone> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Get next pending milestone
    @Query("SELECT m FROM PaymentMilestone m WHERE m.acquisition = :acquisition " +
            "AND m.status = 'PENDING' ORDER BY m.milestoneNumber ASC")
    List<PaymentMilestone> findNextPendingMilestone(@Param("acquisition") Acquisition acquisition,
                                                    Pageable pageable);
}
package repository;

import entity.Acquisition;
import entity.AcquisitionParticipant;
import entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;  // ADD THIS IMPORT
import java.util.List;
import java.util.Optional;

@Repository
public interface AcquisitionParticipantRepository extends JpaRepository<AcquisitionParticipant, Long> {

    // Find participant by acquisition and user
    Optional<AcquisitionParticipant> findByAcquisitionAndUser(Acquisition acquisition, User user);

    // Find all participants of an acquisition
    List<AcquisitionParticipant> findByAcquisition(Acquisition acquisition);

    // Find participants by status in an acquisition
    List<AcquisitionParticipant> findByAcquisitionAndStatus(Acquisition acquisition,
                                                            AcquisitionParticipant.ParticipantStatus status);

    // Find all acquisitions a user has joined
    Page<AcquisitionParticipant> findByUser(User user, Pageable pageable);

    // Count participants in an acquisition
    long countByAcquisition(Acquisition acquisition);

    // Count participants by status in an acquisition
    long countByAcquisitionAndStatus(Acquisition acquisition,
                                     AcquisitionParticipant.ParticipantStatus status);

    // Get total quantity joined for an acquisition
    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM AcquisitionParticipant p WHERE p.acquisition = :acquisition")
    int getTotalQuantityByAcquisition(@Param("acquisition") Acquisition acquisition);

    // Get total contribution amount for an acquisition
    @Query("SELECT COALESCE(SUM(p.contributionAmount), 0) FROM AcquisitionParticipant p " +
            "WHERE p.acquisition = :acquisition AND p.status = 'APPROVED'")
    BigDecimal getTotalContributionByAcquisition(@Param("acquisition") Acquisition acquisition);

    // Check if user has already joined an acquisition
    boolean existsByAcquisitionAndUser(Acquisition acquisition, User user);

    // Find pending participants for an acquisition
    List<AcquisitionParticipant> findByAcquisitionAndStatusOrderByJoinedAtAsc(
            Acquisition acquisition, AcquisitionParticipant.ParticipantStatus status);

    // Find participants with pending payments
    @Query("SELECT p FROM AcquisitionParticipant p WHERE p.acquisition = :acquisition " +
            "AND p.status = 'APPROVED' AND p NOT IN " +
            "(SELECT pa.participant FROM AcquisitionPayment pa WHERE pa.status = 'COMPLETED')")
    List<AcquisitionParticipant> findParticipantsWithPendingPayments(@Param("acquisition") Acquisition acquisition);

    // Get all participants for an acquisition with their payment status
    @Query("SELECT p FROM AcquisitionParticipant p " +
            "LEFT JOIN FETCH p.payments " +
            "WHERE p.acquisition = :acquisition")
    List<AcquisitionParticipant> findParticipantsWithPayments(@Param("acquisition") Acquisition acquisition);
}
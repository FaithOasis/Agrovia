package com.agrosolutions.AgroVia.repository;
import com.agrosolutions.AgroVia.entity.Acquisition;
import com.agrosolutions.AgroVia.entity.AcquisitionParticipant;
import com.agrosolutions.AgroVia.entity.AcquisitionPayment;
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
public interface AcquisitionPaymentRepository extends JpaRepository<AcquisitionPayment, Long> {
    // Find payments by participant
    List<AcquisitionPayment> findByParticipant(AcquisitionParticipant participant);

    // Find payments by acquisition
    Page<AcquisitionPayment> findByAcquisition(Acquisition acquisition, Pageable pageable);

    // Find payments by status
    List<AcquisitionPayment> findByStatus(AcquisitionPayment.PaymentStatus status);

    // Find payments by payment method
    List<AcquisitionPayment> findByPaymentMethod(AcquisitionPayment.PaymentMethod method);

    // Find payment by transaction ID
    AcquisitionPayment findByTransactionId(String transactionId);

    // Get total amount collected for an acquisition
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM AcquisitionPayment p " +
            "WHERE p.acquisition = :acquisition AND p.status = 'COMPLETED'")
    BigDecimal getTotalCollectedForAcquisition(@Param("acquisition") Acquisition acquisition);

    // Get total amount paid by a participant
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM AcquisitionPayment p " +
            "WHERE p.participant = :participant AND p.status = 'COMPLETED'")
    BigDecimal getTotalPaidByParticipant(@Param("participant") AcquisitionParticipant participant);

    // Find payments due soon
    @Query("SELECT p FROM AcquisitionPayment p WHERE p.status = 'PENDING' " +
            "AND p.dueDate BETWEEN :now AND :soon")
    List<AcquisitionPayment> findPaymentsDueSoon(@Param("now") LocalDateTime now,
                                                 @Param("soon") LocalDateTime soon);

    // Find overdue payments
    @Query("SELECT p FROM AcquisitionPayment p WHERE p.status = 'PENDING' " +
            "AND p.dueDate < CURRENT_TIMESTAMP")
    List<AcquisitionPayment> findOverduePayments();

    // Get payment statistics for an acquisition
    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.amount) FROM AcquisitionPayment p " +
            "WHERE p.acquisition = :acquisition AND p.status = 'COMPLETED' " +
            "GROUP BY p.paymentMethod")
    List<Object[]> getPaymentStatistics(@Param("acquisition") Acquisition acquisition);

    // Find payments made between dates
    List<AcquisitionPayment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}

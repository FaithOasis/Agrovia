package repository;

import entity.Acquisition;
import entity.DeliveryTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryTrackingRepository extends JpaRepository<DeliveryTracking, Long> {

    // Find all tracking entries for an acquisition (ordered by date)
    List<DeliveryTracking> findByAcquisitionOrderByCreatedAtDesc(Acquisition acquisition);

    // Find latest tracking entry for an acquisition
    DeliveryTracking findFirstByAcquisitionOrderByCreatedAtDesc(Acquisition acquisition);

    // Find tracking entries by status
    List<DeliveryTracking> findByStatus(DeliveryTracking.DeliveryStatus status);

    // Find acquisitions with delivery status
    @Query("SELECT d.acquisition FROM DeliveryTracking d WHERE d.status = :status")
    List<Acquisition> findAcquisitionsByDeliveryStatus(@Param("status") DeliveryTracking.DeliveryStatus status);

    // Find deliveries that are delayed
    @Query("SELECT d FROM DeliveryTracking d WHERE d.status NOT IN ('DELIVERED', 'FAILED') " +
            "AND d.estimatedDelivery < CURRENT_TIMESTAMP")
    List<DeliveryTracking> findDelayedDeliveries();

    // Find deliveries by tracking number
    Optional<DeliveryTracking> findByTrackingNumber(String trackingNumber);

    // Find deliveries by carrier
    List<DeliveryTracking> findByCarrierContaining(String carrier);

    // Get delivery history for an acquisition
    @Query("SELECT d FROM DeliveryTracking d WHERE d.acquisition = :acquisition " +
            "ORDER BY d.createdAt ASC")
    List<DeliveryTracking> getDeliveryHistory(@Param("acquisition") Acquisition acquisition);

    // Count deliveries by status
    long countByStatus(DeliveryTracking.DeliveryStatus status);

    // Find deliveries updated within date range
    List<DeliveryTracking> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Get delivery progress for an acquisition
    @Query("SELECT d.status, COUNT(d) FROM DeliveryTracking d " +
            "WHERE d.acquisition = :acquisition GROUP BY d.status")
    List<Object[]> getDeliveryProgress(@Param("acquisition") Acquisition acquisition);
}
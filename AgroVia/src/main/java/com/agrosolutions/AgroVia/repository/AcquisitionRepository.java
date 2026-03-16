package com.agrosolutions.AgroVia.repository;
import com.agrosolutions.AgroVia.entity.Acquisition;
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
public interface AcquisitionRepository extends JpaRepository<Acquisition, Long>  {
    // Find acquisitions by creator
    Page<Acquisition> findByCreator(User creator, Pageable pageable);

    // Find acquisitions by status
    Page<Acquisition> findByStatus(Acquisition.AcquisitionStatus status, Pageable pageable);

    // Find acquisitions by type (BUYING/SELLING)
    Page<Acquisition> findByType(Acquisition.AcquisitionType type, Pageable pageable);

    // Find open acquisitions (available to join)
    @Query("SELECT a FROM Acquisition a WHERE a.status = 'OPEN' AND a.endDate > CURRENT_TIMESTAMP AND a.currentQuantity < a.targetQuantity")
    Page<Acquisition> findOpenAcquisitions(Pageable pageable);

    // Search acquisitions with filters
    @Query("SELECT a FROM Acquisition a WHERE " +
            "(:type IS NULL OR a.type = :type) AND " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:minPrice IS NULL OR a.unitPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR a.unitPrice <= :maxPrice) AND " +
            "(:location IS NULL OR LOWER(a.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:searchTerm IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Acquisition> searchAcquisitions(@Param("type") Acquisition.AcquisitionType type,
                                         @Param("status") Acquisition.AcquisitionStatus status,
                                         @Param("minPrice") BigDecimal minPrice,
                                         @Param("maxPrice") BigDecimal maxPrice,
                                         @Param("location") String location,
                                         @Param("searchTerm") String searchTerm,
                                         Pageable pageable);

    // Find acquisitions ending soon
    @Query("SELECT a FROM Acquisition a WHERE a.status = 'OPEN' AND a.endDate BETWEEN :now AND :soon")
    List<Acquisition> findAcquisitionsEndingSoon(@Param("now") LocalDateTime now,
                                                 @Param("soon") LocalDateTime soon);

    // Find acquisitions that have reached target
    @Query("SELECT a FROM Acquisition a WHERE a.status = 'OPEN' AND a.currentQuantity >= a.targetQuantity")
    List<Acquisition> findAcquisitionsThatReachedTarget();

    // Count acquisitions by status for a user
    @Query("SELECT COUNT(a) FROM Acquisition a WHERE a.creator = :user AND a.status = :status")
    long countByCreatorAndStatus(@Param("user") User user,
                                 @Param("status") Acquisition.AcquisitionStatus status);

    // Find expired acquisitions (past end date and still open)
    @Query("SELECT a FROM Acquisition a WHERE a.status = 'OPEN' AND a.endDate < CURRENT_TIMESTAMP")
    List<Acquisition> findExpiredAcquisitions();

    // Find acquisitions by product
    Page<Acquisition> findByProductId(Long productId, Pageable pageable);

    // Find acquisitions with minimum participants requirement not met
    @Query("SELECT a FROM Acquisition a WHERE a.status = 'OPEN' AND SIZE(a.participants) < a.minParticipants")
    List<Acquisition> findAcquisitionsWithInsufficientParticipants();
}

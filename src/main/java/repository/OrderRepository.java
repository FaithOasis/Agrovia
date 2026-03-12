package repository;

import entity.Order;
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
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find orders by buyer (customer)
    Page<Order> findByBuyer(User buyer, Pageable pageable);

    // Find orders by seller
    Page<Order> findBySeller(User seller, Pageable pageable);

    // Find orders by status
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    // Find order by order number
    Optional<Order> findByOrderNumber(String orderNumber);

    // Search orders with multiple filters
    @Query("SELECT o FROM Order o WHERE " +
            "(:buyerId IS NULL OR o.buyer.id = :buyerId) AND " +
            "(:sellerId IS NULL OR o.seller.id = :sellerId) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:startDate IS NULL OR o.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR o.createdAt <= :endDate)")
    Page<Order> searchOrders(@Param("buyerId") Long buyerId,
                             @Param("sellerId") Long sellerId,
                             @Param("status") Order.OrderStatus status,
                             @Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate,
                             Pageable pageable);

    // Find orders by seller and status
    List<Order> findBySellerAndStatus(User seller, Order.OrderStatus status);

    // Find orders by buyer and status
    List<Order> findByBuyerAndStatus(User buyer, Order.OrderStatus status);

    // Find recent orders for a user (both as buyer or seller)
    @Query("SELECT o FROM Order o WHERE o.buyer = :user OR o.seller = :user ORDER BY o.createdAt DESC")
    Page<Order> findRecentOrdersForUser(@Param("user") User user, Pageable pageable);

    // Count orders by status for a seller
    Long countBySellerAndStatus(User seller, Order.OrderStatus status);

    // Count orders by status for a buyer
    Long countByBuyerAndStatus(User buyer, Order.OrderStatus status);

    // Find orders created between dates
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find orders by payment status
    Page<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus, Pageable pageable);

    // Find orders that are delayed (created more than X days ago and not delivered)
    @Query("SELECT o FROM Order o WHERE o.status NOT IN ('DELIVERED', 'CANCELLED', 'REFUNDED') AND o.createdAt < :date")
    List<Order> findPendingOrdersOlderThan(@Param("date") LocalDateTime date);
}
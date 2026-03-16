package com.agrosolutions.AgroVia.repository;

import com.agrosolutions.AgroVia.entity.Order;
import com.agrosolutions.AgroVia.entity.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Long> {

    // Find all tracking entries for an order, sorted by date (most recent first)
    List<OrderTracking> findByOrderOrderByCreatedAtDesc(Order order);

    // Find latest tracking entry for an order
    OrderTracking findFirstByOrderOrderByCreatedAtDesc(Order order);

    // Find tracking entries by status
    List<OrderTracking> findByStatus(Order.OrderStatus status);

    // Find tracking entries by location
    List<OrderTracking> findByLocationContaining(String location);

    // Count tracking entries for an order
    Long countByOrder(Order order);

    // Delete all tracking entries for an order
    void deleteByOrder(Order order);
}
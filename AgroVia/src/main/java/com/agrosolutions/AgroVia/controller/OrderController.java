package com.agrosolutions.AgroVia.controller;

import com.agrosolutions.AgroVia.dto.*;
import com.agrosolutions.AgroVia.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Place a new order
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return new ResponseEntity<>(ApiResponse.success("Order placed successfully", response), HttpStatus.CREATED);
    }

    // Get my purchases (as buyer)
    @GetMapping("/my-purchases")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyPurchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderResponse> orders = orderService.getMyPurchases(pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    // Get my sales (as seller)
    @GetMapping("/my-sales")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMySales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderResponse> orders = orderService.getMySales(pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    // Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Update order status
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location) {
        OrderResponse response = orderService.updateOrderStatus(id, status, description, location);
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", response));
    }

    // Get order tracking history
    @GetMapping("/{orderId}/tracking")
    public ResponseEntity<ApiResponse<List<OrderTrackingResponse>>> getOrderTrackingHistory(
            @PathVariable Long orderId) {
        List<OrderTrackingResponse> trackingHistory = orderService.getOrderTrackingHistory(orderId);
        return ResponseEntity.ok(ApiResponse.success(trackingHistory));
    }

    // Search orders with filters
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> searchOrders(
            @RequestParam(required = false) Long buyerId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderResponse> orders = orderService.searchOrders(
                buyerId, sellerId, status, startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    // Cancel order
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long id) {
        OrderResponse response = orderService.cancelOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", response));
    }
}

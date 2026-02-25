package service;

import com.agrosolutions.AgroVia.dto.*;
import entity.*;
import com.agrosolutions.AgroVia.exception.BadRequestException;
import com.agrosolutions.AgroVia.exception.ResourceNotFoundException;
import com.agrosolutions.AgroVia.exception.UnauthorizedException;
import entity.MarketPlace.Negotiation;
import entity.MarketPlace.Order;
import entity.MarketPlace.OrderTracking;
import entity.MarketPlace.Product;
import repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderTrackingRepository orderTrackingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NegotiationRepository negotiationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Get current logged in user
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Create new order
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User currentUser = getCurrentUser();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check if product is available
        if (product.getStatus() != Product.ProductStatus.AVAILABLE) {
            throw new BadRequestException("Product is not available");
        }

        // Check if quantity is available
        if (request.getQuantity() > product.getQuantityAvailable()) {
            throw new BadRequestException("Requested quantity exceeds available stock");
        }

        // Check if user is not buying their own product
        if (product.getSeller().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You cannot buy your own product");
        }

        BigDecimal unitPrice;
        Negotiation negotiation = null;

        // If order comes from negotiation
        if (request.getNegotiationId() != null) {
            negotiation = negotiationRepository.findById(request.getNegotiationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Negotiation not found"));

            // Validate negotiation
            if (!negotiation.getProduct().getId().equals(product.getId())) {
                throw new BadRequestException("Negotiation does not match the product");
            }

            if (negotiation.getStatus() != Negotiation.NegotiationStatus.ACCEPTED) {
                throw new BadRequestException("Negotiation must be accepted to create an order");
            }

            unitPrice = negotiation.getOfferedPrice();

            // Update negotiation status
            negotiation.setStatus(Negotiation.NegotiationStatus.CONVERTED_TO_ORDER);
            negotiationRepository.save(negotiation);
        } else {
            unitPrice = product.getPrice();
        }

        // Calculate total amount
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

        // Create order
        Order order = new Order();
        order.setBuyer(currentUser);
        order.setSeller(product.getSeller());
        order.setProduct(product);
        order.setNegotiation(negotiation);
        order.setQuantity(request.getQuantity());
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(totalAmount);
        order.setShippingAddress(request.getShippingAddress());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setPaymentMethod(Order.PaymentMethod.valueOf(request.getPaymentMethod()));
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setNotes(request.getNotes());

        Order savedOrder = orderRepository.save(order);

        // Create initial tracking entry
        createOrderTracking(savedOrder, Order.OrderStatus.PENDING,
                "Order placed successfully", "System");

        // Update product quantity
        product.setQuantityAvailable(product.getQuantityAvailable() - request.getQuantity());
        if (product.getQuantityAvailable() == 0) {
            product.setStatus(Product.ProductStatus.OUT_OF_STOCK);
        }
        productRepository.save(product);

        // Create notification for seller - FIXED: Using createSystemAnnouncement
        String message = String.format("New order received for %s - Quantity: %d %s",
                product.getTitle(), request.getQuantity(), product.getUnit());
        notificationService.createSystemAnnouncement(message, product.getSeller());

        return mapToOrderResponse(savedOrder);
    }

    // Create order tracking entry
    @Transactional
    public OrderTracking createOrderTracking(Order order, Order.OrderStatus status,
                                             String description, String updatedBy) {
        OrderTracking tracking = new OrderTracking();
        tracking.setOrder(order);
        tracking.setStatus(status);
        tracking.setDescription(description);
        tracking.setUpdatedBy(updatedBy);

        return orderTrackingRepository.save(tracking);
    }

    // Update order status
    @Transactional
    public OrderResponse updateOrderStatus(Long id, String status, String description, String location) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User currentUser = getCurrentUser();
        // Only seller can update order status
        if (!order.getSeller().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the seller can update order status");
        }

        Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status);
        order.setStatus(newStatus);

        // If location provided, set it
        if (location != null && !location.isEmpty()) {
            // You might need to add a location field to your tracking creation
        }

        // If delivered, set delivery date
        if (newStatus == Order.OrderStatus.DELIVERED) {
            order.setDeliveryDate(LocalDateTime.now());
            order.setPaymentStatus(Order.PaymentStatus.PAID);
        }

        Order updatedOrder = orderRepository.save(order);

        // Create tracking entry
        createOrderTracking(order, newStatus, description, currentUser.getUsername());

        // Create notification for buyer - FIXED: Using createSystemAnnouncement
        String message = String.format("Order %s status updated to: %s",
                order.getOrderNumber(), newStatus);
        notificationService.createSystemAnnouncement(message, order.getBuyer());

        return mapToOrderResponse(updatedOrder);
    }

    // Get my purchases (as buyer)
    public Page<OrderResponse> getMyPurchases(Pageable pageable) {
        User currentUser = getCurrentUser();
        return orderRepository.findByBuyer(currentUser, pageable)
                .map(this::mapToOrderResponse);
    }

    // Get my sales (as seller)
    public Page<OrderResponse> getMySales(Pageable pageable) {
        User currentUser = getCurrentUser();
        return orderRepository.findBySeller(currentUser, pageable)
                .map(this::mapToOrderResponse);
    }

    // Get order by ID
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User currentUser = getCurrentUser();
        if (!order.getBuyer().getId().equals(currentUser.getId()) &&
                !order.getSeller().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to view this order");
        }

        return mapToOrderResponse(order);
    }

    // Get order tracking history
    public List<OrderTrackingResponse> getOrderTrackingHistory(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User currentUser = getCurrentUser();
        if (!order.getBuyer().getId().equals(currentUser.getId()) &&
                !order.getSeller().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to view this order");
        }

        return orderTrackingRepository.findByOrderOrderByCreatedAtDesc(order)
                .stream()
                .map(this::mapToOrderTrackingResponse)
                .collect(Collectors.toList());
    }

    // Search orders with filters
    public Page<OrderResponse> searchOrders(Long buyerId, Long sellerId, String status,
                                            LocalDateTime startDate, LocalDateTime endDate,
                                            Pageable pageable) {
        Order.OrderStatus orderStatus = status != null ? Order.OrderStatus.valueOf(status) : null;

        return orderRepository.searchOrders(buyerId, sellerId, orderStatus, startDate, endDate, pageable)
                .map(this::mapToOrderResponse);
    }

    // Cancel order
    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User currentUser = getCurrentUser();
        // Only buyer can cancel order
        if (!order.getBuyer().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the buyer can cancel this order");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BadRequestException("Only pending orders can be cancelled");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);

        // Return product quantity to stock
        Product product = order.getProduct();
        product.setQuantityAvailable(product.getQuantityAvailable() + order.getQuantity());
        productRepository.save(product);

        // Create tracking entry
        createOrderTracking(order, Order.OrderStatus.CANCELLED,
                "Order cancelled by buyer", currentUser.getUsername());

        // Create notification for seller - FIXED: Using createSystemAnnouncement
        String message = String.format("Order %s has been cancelled by the buyer",
                order.getOrderNumber());
        notificationService.createSystemAnnouncement(message, order.getSeller());

        return mapToOrderResponse(cancelledOrder);
    }

    // Map Order entity to OrderResponse DTO
    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setQuantity(order.getQuantity());
        response.setUnitPrice(order.getUnitPrice());
        response.setTotalAmount(order.getTotalAmount());
        response.setShippingAddress(order.getShippingAddress());
        response.setPhoneNumber(order.getPhoneNumber());
        response.setStatus(order.getStatus().toString());
        response.setPaymentMethod(order.getPaymentMethod().toString());
        response.setPaymentStatus(order.getPaymentStatus().toString());
        response.setNotes(order.getNotes());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setDeliveryDate(order.getDeliveryDate());

        // Map buyer - FIXED: Removed phoneNumber
        if (order.getBuyer() != null) {
            UserSummary buyerSummary = new UserSummary(
                    order.getBuyer().getId(),
                    order.getBuyer().getUsername(),
                    order.getBuyer().getEmail(),
                    order.getBuyer().getFullName()
            );
            response.setBuyer(buyerSummary);
        }

        // Map seller - FIXED: Removed phoneNumber
        if (order.getSeller() != null) {
            UserSummary sellerSummary = new UserSummary(
                    order.getSeller().getId(),
                    order.getSeller().getUsername(),
                    order.getSeller().getEmail(),
                    order.getSeller().getFullName()
            );
            response.setSeller(sellerSummary);
        }

        // Map product
        if (order.getProduct() != null) {
            ProductSummary productSummary = new ProductSummary(
                    order.getProduct().getId(),
                    order.getProduct().getTitle(),
                    order.getProduct().getPrice(),
                    order.getProduct().getUnit(),
                    order.getProduct().getImageUrl()
            );
            response.setProduct(productSummary);
        }

        // Map negotiation
        if (order.getNegotiation() != null) {
            NegotiationSummary negotiationSummary = new NegotiationSummary(
                    order.getNegotiation().getId(),
                    order.getNegotiation().getOfferedPrice(),
                    order.getNegotiation().getStatus().toString()
            );
            response.setNegotiation(negotiationSummary);
        }

        // Map tracking history
        if (order.getTrackingHistory() != null && !order.getTrackingHistory().isEmpty()) {
            response.setTrackingHistory(
                    order.getTrackingHistory().stream()
                            .map(this::mapToOrderTrackingResponse)
                            .collect(Collectors.toList())
            );
        }

        return response;
    }

    // Map OrderTracking entity to OrderTrackingResponse DTO
    private OrderTrackingResponse mapToOrderTrackingResponse(OrderTracking tracking) {
        OrderTrackingResponse response = new OrderTrackingResponse();
        response.setId(tracking.getId());
        response.setStatus(tracking.getStatus().toString());
        response.setLocation(tracking.getLocation());
        response.setDescription(tracking.getDescription());
        response.setUpdatedBy(tracking.getUpdatedBy());
        response.setCreatedAt(tracking.getCreatedAt());
        return response;
    }
}
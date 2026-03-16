package com.agrosolutions.AgroVia.service;

import com.agrosolutions.AgroVia.dto.*;
import com.agrosolutions.AgroVia.entity.User;
import com.agrosolutions.AgroVia.exception.BadRequestException;
import com.agrosolutions.AgroVia.exception.ResourceNotFoundException;
import com.agrosolutions.AgroVia.exception.UnauthorizedException;
import com.agrosolutions.AgroVia.entity.Negotiation;
import com.agrosolutions.AgroVia.entity.Product;
import com.agrosolutions.AgroVia.repository.NegotiationRepository;
import com.agrosolutions.AgroVia.repository.ProductRepository;
import com.agrosolutions.AgroVia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class NegotiationService {

    @Autowired
    private NegotiationRepository negotiationRepository;

    @Autowired
    private ProductRepository productRepository;

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

    // Create new negotiation
    @Transactional
    public NegotiationResponse createNegotiation(NegotiationRequest request) {
        User currentUser = getCurrentUser();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check if product is negotiable
        if (!product.getIsNegotiable()) {
            throw new BadRequestException("This product is not negotiable");
        }

        // Check if user is not the seller
        if (product.getSeller().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You cannot negotiate on your own product");
        }

        // Check if there's already a pending negotiation
        if (negotiationRepository.existsByProductAndBuyerAndStatus(product, currentUser,
                Negotiation.NegotiationStatus.PENDING)) {
            throw new BadRequestException("You already have a pending negotiation for this product");
        }

        // Validate offered price
        if (request.getOfferedPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Offered price must be greater than zero");
        }

        // Validate quantity
        if (request.getQuantity() > product.getQuantityAvailable()) {
            throw new BadRequestException("Requested quantity exceeds available stock");
        }

        Negotiation negotiation = new Negotiation();
        negotiation.setProduct(product);
        negotiation.setBuyer(currentUser);
        negotiation.setOfferedPrice(request.getOfferedPrice());
        negotiation.setQuantity(request.getQuantity());
        negotiation.setMessage(request.getMessage());
        negotiation.setStatus(Negotiation.NegotiationStatus.PENDING);

        Negotiation savedNegotiation = negotiationRepository.save(negotiation);

        // Create notification for seller using createSystemAnnouncement
        String message = String.format("New negotiation for %s - Offer: %s %s",
                product.getTitle(), request.getOfferedPrice(), product.getUnit());
        notificationService.createSystemAnnouncement(message, product.getSeller());

        return mapToNegotiationResponse(savedNegotiation);
    }

    // Respond to negotiation (accept/reject)
    @Transactional
    public NegotiationResponse respondToNegotiation(Long id, String response, String status) {
        Negotiation negotiation = negotiationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Negotiation not found"));

        User currentUser = getCurrentUser();
        if (!negotiation.getProduct().getSeller().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the seller can respond to this negotiation");
        }

        if (negotiation.getStatus() != Negotiation.NegotiationStatus.PENDING) {
            throw new BadRequestException("This negotiation is no longer pending");
        }

        Negotiation.NegotiationStatus negotiationStatus = Negotiation.NegotiationStatus.valueOf(status);
        negotiation.setStatus(negotiationStatus);
        negotiation.setSellerResponse(response);

        if (negotiationStatus == Negotiation.NegotiationStatus.ACCEPTED) {
            // Create notification for buyer using createSystemAnnouncement
            String message = String.format("Your negotiation for %s has been accepted!",
                    negotiation.getProduct().getTitle());
            notificationService.createSystemAnnouncement(message, negotiation.getBuyer());
        } else if (negotiationStatus == Negotiation.NegotiationStatus.REJECTED) {
            String message = String.format("Your negotiation for %s has been rejected: %s",
                    negotiation.getProduct().getTitle(), response);
            notificationService.createSystemAnnouncement(message, negotiation.getBuyer());
        }

        Negotiation updatedNegotiation = negotiationRepository.save(negotiation);
        return mapToNegotiationResponse(updatedNegotiation);
    }

    // Get my negotiations (as buyer)
    public Page<NegotiationResponse> getMyNegotiations(Pageable pageable) {
        User currentUser = getCurrentUser();
        return negotiationRepository.findByBuyer(currentUser, pageable)
                .map(this::mapToNegotiationResponse);
    }

    // Get negotiations for my products (as seller)
    public Page<NegotiationResponse> getNegotiationsForMyProducts(Pageable pageable) {
        User currentUser = getCurrentUser();
        return negotiationRepository.findBySeller(currentUser, pageable)
                .map(this::mapToNegotiationResponse);
    }

    // Get negotiation by ID
    public NegotiationResponse getNegotiationById(Long id) {
        Negotiation negotiation = negotiationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Negotiation not found"));

        User currentUser = getCurrentUser();
        if (!negotiation.getBuyer().getId().equals(currentUser.getId()) &&
                !negotiation.getProduct().getSeller().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to view this negotiation");
        }

        return mapToNegotiationResponse(negotiation);
    }

    // Map Negotiation entity to NegotiationResponse DTO
    private NegotiationResponse mapToNegotiationResponse(Negotiation negotiation) {
        NegotiationResponse response = new NegotiationResponse();
        response.setId(negotiation.getId());
        response.setOfferedPrice(negotiation.getOfferedPrice());
        response.setQuantity(negotiation.getQuantity());
        response.setMessage(negotiation.getMessage());
        response.setStatus(negotiation.getStatus().toString());
        response.setSellerResponse(negotiation.getSellerResponse());
        response.setCreatedAt(negotiation.getCreatedAt());
        response.setUpdatedAt(negotiation.getUpdatedAt());

        // Map product
        ProductSummary productSummary = new ProductSummary(
                negotiation.getProduct().getId(),
                negotiation.getProduct().getTitle(),
                negotiation.getProduct().getPrice(),
                negotiation.getProduct().getUnit(),
                negotiation.getProduct().getImageUrl()
        );
        response.setProduct(productSummary);

        // Map buyer - FIXED: Removed phoneNumber parameter
        UserSummary buyerSummary = new UserSummary(
                negotiation.getBuyer().getId(),
                negotiation.getBuyer().getUsername(),
                negotiation.getBuyer().getEmail(),
                negotiation.getBuyer().getFullName()
        );
        response.setBuyer(buyerSummary);

        return response;
    }
}
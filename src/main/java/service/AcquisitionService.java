package service;

import com.agrosolutions.AgroVia.dto.*;
import entity.*;
import com.agrosolutions.AgroVia.exception.BadRequestException;
import com.agrosolutions.AgroVia.exception.ResourceNotFoundException;
import com.agrosolutions.AgroVia.exception.UnauthorizedException;
import repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AcquisitionService {

    @Autowired
    private AcquisitionRepository acquisitionRepository;

    @Autowired
    private AcquisitionParticipantRepository participantRepository;

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

    // Create new acquisition proposal
    @Transactional
    public AcquisitionResponse createAcquisition(AcquisitionRequest request) {
        User currentUser = getCurrentUser();

        // Validate request
        validateAcquisitionRequest(request);

        Acquisition acquisition = new Acquisition();
        acquisition.setTitle(request.getTitle());
        acquisition.setDescription(request.getDescription());
        acquisition.setType(Acquisition.AcquisitionType.valueOf(request.getType()));
        acquisition.setCreator(currentUser);
        acquisition.setTargetQuantity(request.getTargetQuantity());
        acquisition.setUnitPrice(request.getUnitPrice());
        acquisition.setMinContribution(request.getMinContribution());
        acquisition.setMaxContribution(request.getMaxContribution());
        acquisition.setMinParticipants(request.getMinParticipants());
        acquisition.setMaxParticipants(request.getMaxParticipants());
        acquisition.setStartDate(request.getStartDate());
        acquisition.setEndDate(request.getEndDate());
        acquisition.setDeliveryDate(request.getDeliveryDate());
        acquisition.setLocation(request.getLocation());
        acquisition.setTermsConditions(request.getTermsConditions());
        acquisition.setStatus(Acquisition.AcquisitionStatus.DRAFT);

        // Set product if provided
        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            acquisition.setProduct(product);
        }

        Acquisition savedAcquisition = acquisitionRepository.save(acquisition);

        // Create notification for creator
        String message = "Your acquisition proposal \"" + request.getTitle() + "\" has been created as draft.";
        notificationService.createSystemAnnouncement(message, currentUser);

        return mapToAcquisitionResponse(savedAcquisition);
    }

    // Validate acquisition request
    private void validateAcquisitionRequest(AcquisitionRequest request) {
        if (request.getTargetQuantity() <= 0) {
            throw new BadRequestException("Target quantity must be greater than zero");
        }
        if (request.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Unit price must be greater than zero");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }
        if (request.getMinContribution() != null && request.getMinContribution() > request.getTargetQuantity()) {
            throw new BadRequestException("Minimum contribution cannot exceed target quantity");
        }
        if (request.getMaxContribution() != null && request.getMaxContribution() < request.getMinContribution()) {
            throw new BadRequestException("Maximum contribution must be greater than minimum contribution");
        }
    }

    // Publish acquisition (change from DRAFT to OPEN)
    @Transactional
    public AcquisitionResponse publishAcquisition(Long id) {
        Acquisition acquisition = acquisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition not found"));

        User currentUser = getCurrentUser();
        if (!acquisition.getCreator().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the creator can publish this acquisition");
        }

        if (acquisition.getStatus() != Acquisition.AcquisitionStatus.DRAFT) {
            throw new BadRequestException("Only draft acquisitions can be published");
        }

        acquisition.setStatus(Acquisition.AcquisitionStatus.OPEN);
        Acquisition updatedAcquisition = acquisitionRepository.save(acquisition);

        // Create notification for followers (optional)
        String message = "New acquisition opened: " + acquisition.getTitle();
        // You could notify relevant users here

        return mapToAcquisitionResponse(updatedAcquisition);
    }

    // Get all open acquisitions
    public Page<AcquisitionSummary> getOpenAcquisitions(Pageable pageable) {
        return acquisitionRepository.findByStatus(Acquisition.AcquisitionStatus.OPEN, pageable)
                .map(this::mapToAcquisitionSummary);
    }

    // Get acquisition by ID
    public AcquisitionResponse getAcquisitionById(Long id) {
        Acquisition acquisition = acquisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition not found"));
        return mapToAcquisitionResponse(acquisition);
    }

    // Update acquisition
    @Transactional
    public AcquisitionResponse updateAcquisition(Long id, AcquisitionRequest request) {
        Acquisition acquisition = acquisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition not found"));

        User currentUser = getCurrentUser();
        if (!acquisition.getCreator().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the creator can update this acquisition");
        }

        // Only allow updates if status is DRAFT or OPEN
        if (acquisition.getStatus() != Acquisition.AcquisitionStatus.DRAFT &&
                acquisition.getStatus() != Acquisition.AcquisitionStatus.OPEN) {
            throw new BadRequestException("Cannot update acquisition in " + acquisition.getStatus() + " status");
        }

        // Update fields
        acquisition.setTitle(request.getTitle());
        acquisition.setDescription(request.getDescription());
        acquisition.setTargetQuantity(request.getTargetQuantity());
        acquisition.setUnitPrice(request.getUnitPrice());
        acquisition.setMinContribution(request.getMinContribution());
        acquisition.setMaxContribution(request.getMaxContribution());
        acquisition.setMinParticipants(request.getMinParticipants());
        acquisition.setMaxParticipants(request.getMaxParticipants());
        acquisition.setEndDate(request.getEndDate());
        acquisition.setDeliveryDate(request.getDeliveryDate());
        acquisition.setLocation(request.getLocation());
        acquisition.setTermsConditions(request.getTermsConditions());

        Acquisition updatedAcquisition = acquisitionRepository.save(acquisition);
        return mapToAcquisitionResponse(updatedAcquisition);
    }

    // Close acquisition (stop accepting participants)
    @Transactional
    public AcquisitionResponse closeAcquisition(Long id) {
        Acquisition acquisition = acquisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition not found"));

        User currentUser = getCurrentUser();
        if (!acquisition.getCreator().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the creator can close this acquisition");
        }

        if (acquisition.getStatus() != Acquisition.AcquisitionStatus.OPEN) {
            throw new BadRequestException("Only open acquisitions can be closed");
        }

        acquisition.setStatus(Acquisition.AcquisitionStatus.CLOSED);
        Acquisition updatedAcquisition = acquisitionRepository.save(acquisition);

        // Notify all participants
        String message = "Acquisition \"" + acquisition.getTitle() + "\" has been closed.";
        for (AcquisitionParticipant participant : acquisition.getParticipants()) {
            notificationService.createSystemAnnouncement(message, participant.getUser());
        }

        return mapToAcquisitionResponse(updatedAcquisition);
    }

    // Cancel acquisition
    @Transactional
    public AcquisitionResponse cancelAcquisition(Long id) {
        Acquisition acquisition = acquisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition not found"));

        User currentUser = getCurrentUser();
        if (!acquisition.getCreator().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the creator can cancel this acquisition");
        }

        acquisition.setStatus(Acquisition.AcquisitionStatus.CANCELLED);
        Acquisition updatedAcquisition = acquisitionRepository.save(acquisition);

        // Notify all participants about cancellation
        String message = "Acquisition \"" + acquisition.getTitle() + "\" has been cancelled.";
        for (AcquisitionParticipant participant : acquisition.getParticipants()) {
            notificationService.createSystemAnnouncement(message, participant.getUser());
        }

        return mapToAcquisitionResponse(updatedAcquisition);
    }

    // Get user's acquisitions (as creator)
    public Page<AcquisitionSummary> getMyAcquisitions(Pageable pageable) {
        User currentUser = getCurrentUser();
        return acquisitionRepository.findByCreator(currentUser, pageable)
                .map(this::mapToAcquisitionSummary);
    }

    // Search acquisitions with filters
    public Page<AcquisitionSummary> searchAcquisitions(String type, String status,
                                                       BigDecimal minPrice, BigDecimal maxPrice,
                                                       String location, String searchTerm,
                                                       Pageable pageable) {
        Acquisition.AcquisitionType acquisitionType = type != null ?
                Acquisition.AcquisitionType.valueOf(type) : null;
        Acquisition.AcquisitionStatus acquisitionStatus = status != null ?
                Acquisition.AcquisitionStatus.valueOf(status) : null;

        return acquisitionRepository.searchAcquisitions(acquisitionType, acquisitionStatus,
                        minPrice, maxPrice, location, searchTerm, pageable)
                .map(this::mapToAcquisitionSummary);
    }

    // Check if target is reached and update status if needed
    @Transactional
    public void checkAndUpdateTargetReached(Long acquisitionId) {
        Acquisition acquisition = acquisitionRepository.findById(acquisitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition not found"));

        if (acquisition.isTargetReached() && acquisition.getStatus() == Acquisition.AcquisitionStatus.OPEN) {
            acquisition.setStatus(Acquisition.AcquisitionStatus.CLOSED);
            acquisitionRepository.save(acquisition);

            // Notify creator
            String message = "Your acquisition \"" + acquisition.getTitle() +
                    "\" has reached its target and is now closed.";
            notificationService.createSystemAnnouncement(message, acquisition.getCreator());
        }
    }

    // Map Acquisition to AcquisitionResponse
    private AcquisitionResponse mapToAcquisitionResponse(Acquisition acquisition) {
        AcquisitionResponse response = new AcquisitionResponse();
        response.setId(acquisition.getId());
        response.setTitle(acquisition.getTitle());
        response.setDescription(acquisition.getDescription());
        response.setType(acquisition.getType().toString());
        response.setTargetQuantity(acquisition.getTargetQuantity());
        response.setCurrentQuantity(acquisition.getCurrentQuantity());
        response.setUnitPrice(acquisition.getUnitPrice());
        response.setTotalPrice(acquisition.getTotalPrice());
        response.setMinContribution(acquisition.getMinContribution());
        response.setMaxContribution(acquisition.getMaxContribution());
        response.setMinParticipants(acquisition.getMinParticipants());
        response.setMaxParticipants(acquisition.getMaxParticipants());
        response.setCurrentParticipants(acquisition.getParticipants() != null ?
                acquisition.getParticipants().size() : 0);
        response.setStartDate(acquisition.getStartDate());
        response.setEndDate(acquisition.getEndDate());
        response.setDeliveryDate(acquisition.getDeliveryDate());
        response.setStatus(acquisition.getStatus().toString());
        response.setLocation(acquisition.getLocation());
        response.setTermsConditions(acquisition.getTermsConditions());
        response.setCreatedAt(acquisition.getCreatedAt());
        response.setUpdatedAt(acquisition.getUpdatedAt());

        // Map creator
        if (acquisition.getCreator() != null) {
            UserSummary creatorSummary = new UserSummary(
                    acquisition.getCreator().getId(),
                    acquisition.getCreator().getUsername(),
                    acquisition.getCreator().getEmail(),
                    acquisition.getCreator().getFullName()
            );
            response.setCreator(creatorSummary);
        }

        // Map product
        if (acquisition.getProduct() != null) {
            ProductSummary productSummary = new ProductSummary(
                    acquisition.getProduct().getId(),
                    acquisition.getProduct().getTitle(),
                    acquisition.getProduct().getPrice(),
                    acquisition.getProduct().getUnit(),
                    acquisition.getProduct().getImageUrl()
            );
            response.setProduct(productSummary);
        }

        // Calculate progress percentage
        if (acquisition.getTargetQuantity() != null && acquisition.getTargetQuantity() > 0) {
            double progress = (acquisition.getCurrentQuantity() * 100.0) / acquisition.getTargetQuantity();
            response.setProgressPercentage(Math.min(progress, 100.0));
        }

        return response;
    }

    // Map Acquisition to AcquisitionSummary
    private AcquisitionSummary mapToAcquisitionSummary(Acquisition acquisition) {
        UserSummary creatorSummary = null;
        if (acquisition.getCreator() != null) {
            creatorSummary = new UserSummary(
                    acquisition.getCreator().getId(),
                    acquisition.getCreator().getUsername(),
                    acquisition.getCreator().getEmail(),
                    acquisition.getCreator().getFullName()
            );
        }

        int participantCount = acquisition.getParticipants() != null ?
                acquisition.getParticipants().size() : 0;

        double progressPercentage = 0;
        if (acquisition.getTargetQuantity() != null && acquisition.getTargetQuantity() > 0) {
            progressPercentage = (acquisition.getCurrentQuantity() * 100.0) / acquisition.getTargetQuantity();
            progressPercentage = Math.min(progressPercentage, 100.0);
        }

        return new AcquisitionSummary(
                acquisition.getId(),
                acquisition.getTitle(),
                acquisition.getType().toString(),
                creatorSummary,
                acquisition.getTargetQuantity(),
                acquisition.getCurrentQuantity(),
                acquisition.getUnitPrice(),
                acquisition.getStatus().toString(),
                acquisition.getEndDate(),
                participantCount,
                progressPercentage
        );
    }
}
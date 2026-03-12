package service;

import com.agrosolutions.AgroVia.dto.*;
import entity.*;
import com.agrosolutions.AgroVia.exception.BadRequestException;
import com.agrosolutions.AgroVia.exception.ResourceNotFoundException;
import com.agrosolutions.AgroVia.exception.UnauthorizedException;
import repository.AcquisitionParticipantRepository;
import repository.AcquisitionRepository;
import repository.UserRepository;
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
public class ParticipationService {

    @Autowired
    private AcquisitionParticipantRepository participantRepository;

    @Autowired
    private AcquisitionRepository acquisitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AcquisitionService acquisitionService;

    @Autowired
    private NotificationService notificationService;

    // Get current logged in user
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Join an acquisition
    @Transactional
    public ParticipantSummary joinAcquisition(JoinAcquisitionRequest request) {
        User currentUser = getCurrentUser();
        Acquisition acquisition = acquisitionRepository.findById(request.getAcquisitionId())
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition not found"));

        // Validate joining rules
        validateJoinRequest(acquisition, currentUser, request);

        // Check if user already joined
        if (participantRepository.existsByAcquisitionAndUser(acquisition, currentUser)) {
            throw new BadRequestException("You have already joined this acquisition");
        }

        // Create participant
        AcquisitionParticipant participant = new AcquisitionParticipant(
                acquisition, currentUser, request.getQuantity()
        );
        participant.setNotes(request.getNotes());
        participant.setStatus(AcquisitionParticipant.ParticipantStatus.PENDING);

        // If no approval needed, auto-approve
        if (acquisition.getMinParticipants() == null || acquisition.getMinParticipants() <= 1) {
            participant.setStatus(AcquisitionParticipant.ParticipantStatus.APPROVED);
            participant.setApprovedAt(LocalDateTime.now());
            participant.setApprovedBy(acquisition.getCreator().getId());
        }

        AcquisitionParticipant savedParticipant = participantRepository.save(participant);

        // Update acquisition current quantity
        acquisition.setCurrentQuantity(acquisition.getCurrentQuantity() + request.getQuantity());
        acquisitionRepository.save(acquisition);

        // Check if target reached
        acquisitionService.checkAndUpdateTargetReached(acquisition.getId());

        // Notify creator
        String message = currentUser.getUsername() + " has requested to join your acquisition \"" +
                acquisition.getTitle() + "\" with quantity " + request.getQuantity();
        notificationService.createSystemAnnouncement(message, acquisition.getCreator());

        return mapToParticipantSummary(savedParticipant);
    }

    // Validate join request
    private void validateJoinRequest(Acquisition acquisition, User user, JoinAcquisitionRequest request) {
        // Check if acquisition is open
        if (!acquisition.isOpen()) {
            throw new BadRequestException("This acquisition is not open for joining");
        }

        // Check if user is not the creator
        if (acquisition.getCreator().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot join your own acquisition");
        }

        // Check quantity limits
        if (request.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        if (acquisition.getMinContribution() != null &&
                request.getQuantity() < acquisition.getMinContribution()) {
            throw new BadRequestException("Minimum contribution is " + acquisition.getMinContribution());
        }

        if (acquisition.getMaxContribution() != null &&
                request.getQuantity() > acquisition.getMaxContribution()) {
            throw new BadRequestException("Maximum contribution is " + acquisition.getMaxContribution());
        }

        // Check if adding this quantity would exceed target
        if (acquisition.getCurrentQuantity() + request.getQuantity() > acquisition.getTargetQuantity()) {
            throw new BadRequestException("Requested quantity would exceed target. Remaining: " +
                    (acquisition.getTargetQuantity() - acquisition.getCurrentQuantity()));
        }

        // Check participant limits
        if (acquisition.getMaxParticipants() != null) {
            long currentParticipants = participantRepository.countByAcquisition(acquisition);
            if (currentParticipants >= acquisition.getMaxParticipants()) {
                throw new BadRequestException("Maximum number of participants reached");
            }
        }
    }

    // Approve participant (by creator)
    @Transactional
    public ParticipantSummary approveParticipant(Long participantId) {
        AcquisitionParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        User currentUser = getCurrentUser();
        Acquisition acquisition = participant.getAcquisition();

        // Check if current user is the creator
        if (!acquisition.getCreator().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the creator can approve participants");
        }

        if (participant.getStatus() != AcquisitionParticipant.ParticipantStatus.PENDING) {
            throw new BadRequestException("Only pending participants can be approved");
        }

        participant.setStatus(AcquisitionParticipant.ParticipantStatus.APPROVED);
        participant.setApprovedAt(LocalDateTime.now());
        participant.setApprovedBy(currentUser.getId());

        AcquisitionParticipant updatedParticipant = participantRepository.save(participant);

        // Notify participant
        String message = "Your request to join \"" + acquisition.getTitle() + "\" has been approved.";
        notificationService.createSystemAnnouncement(message, participant.getUser());

        return mapToParticipantSummary(updatedParticipant);
    }

    // Reject participant (by creator)
    @Transactional
    public ParticipantSummary rejectParticipant(Long participantId, String reason) {
        AcquisitionParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        User currentUser = getCurrentUser();
        Acquisition acquisition = participant.getAcquisition();

        // Check if current user is the creator
        if (!acquisition.getCreator().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the creator can reject participants");
        }

        if (participant.getStatus() != AcquisitionParticipant.ParticipantStatus.PENDING) {
            throw new BadRequestException("Only pending participants can be rejected");
        }

        participant.setStatus(AcquisitionParticipant.ParticipantStatus.REJECTED);
        participant.setNotes(reason);

        AcquisitionParticipant updatedParticipant = participantRepository.save(participant);

        // Return quantity to acquisition
        acquisition.setCurrentQuantity(acquisition.getCurrentQuantity() - participant.getQuantity());
        acquisitionRepository.save(acquisition);

        // Notify participant
        String message = "Your request to join \"" + acquisition.getTitle() + "\" has been rejected. " +
                (reason != null ? "Reason: " + reason : "");
        notificationService.createSystemAnnouncement(message, participant.getUser());

        return mapToParticipantSummary(updatedParticipant);
    }

    // Leave acquisition (withdraw)
    @Transactional
    public void leaveAcquisition(Long acquisitionId) {
        User currentUser = getCurrentUser();
        Acquisition acquisition = acquisitionRepository.findById(acquisitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition not found"));

        AcquisitionParticipant participant = participantRepository
                .findByAcquisitionAndUser(acquisition, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("You have not joined this acquisition"));

        // Check if can leave (only if not paid or acquisition still open)
        if (participant.getStatus() == AcquisitionParticipant.ParticipantStatus.PAID) {
            throw new BadRequestException("You cannot leave after making payments");
        }

        participant.setStatus(AcquisitionParticipant.ParticipantStatus.WITHDRAWN);
        participantRepository.save(participant);

        // Return quantity to acquisition
        acquisition.setCurrentQuantity(acquisition.getCurrentQuantity() - participant.getQuantity());
        acquisitionRepository.save(acquisition);

        // Notify creator
        String message = currentUser.getUsername() + " has withdrawn from \"" +
                acquisition.getTitle() + "\"";
        notificationService.createSystemAnnouncement(message, acquisition.getCreator());
    }

    // Get participants of an acquisition
    public List<ParticipantSummary> getAcquisitionParticipants(Long acquisitionId) {
        Acquisition acquisition = acquisitionRepository.findById(acquisitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition not found"));

        User currentUser = getCurrentUser();
        // Only creator or admin can see all participants
        if (!acquisition.getCreator().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the creator can view all participants");
        }

        return participantRepository.findByAcquisition(acquisition)
                .stream()
                .map(this::mapToParticipantSummary)
                .collect(Collectors.toList());
    }

    // Get user's participations (acquisitions they joined)
    public Page<AcquisitionSummary> getMyParticipations(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<AcquisitionParticipant> participations = participantRepository.findByUser(currentUser, pageable);

        return participations.map(p -> {
            Acquisition a = p.getAcquisition();
            UserSummary creatorSummary = new UserSummary(
                    a.getCreator().getId(),
                    a.getCreator().getUsername(),
                    a.getCreator().getEmail(),
                    a.getCreator().getFullName()
            );

            double progressPercentage = 0;
            if (a.getTargetQuantity() != null && a.getTargetQuantity() > 0) {
                progressPercentage = (a.getCurrentQuantity() * 100.0) / a.getTargetQuantity();
                progressPercentage = Math.min(progressPercentage, 100.0);
            }

            return new AcquisitionSummary(
                    a.getId(),
                    a.getTitle(),
                    a.getType().toString(),
                    creatorSummary,
                    a.getTargetQuantity(),
                    a.getCurrentQuantity(),
                    a.getUnitPrice(),
                    a.getStatus().toString(),
                    a.getEndDate(),
                    a.getParticipants().size(),
                    progressPercentage
            );
        });
    }

    // Map AcquisitionParticipant to ParticipantSummary
    private ParticipantSummary mapToParticipantSummary(AcquisitionParticipant participant) {
        ParticipantSummary summary = new ParticipantSummary();
        summary.setId(participant.getId());
        summary.setQuantity(participant.getQuantity());
        summary.setContributionAmount(participant.getContributionAmount());
        summary.setStatus(participant.getStatus().toString());
        summary.setJoinedAt(participant.getJoinedAt());

        // Map user
        if (participant.getUser() != null) {
            UserSummary userSummary = new UserSummary(
                    participant.getUser().getId(),
                    participant.getUser().getUsername(),
                    participant.getUser().getEmail(),
                    participant.getUser().getFullName()
            );
            summary.setUser(userSummary);
        }

        // Calculate amount paid and fully paid status
        if (participant.getPayments() != null && !participant.getPayments().isEmpty()) {
            BigDecimal totalPaid = participant.getTotalPaid();
            summary.setAmountPaid(totalPaid);
            summary.setFullyPaid(participant.isFullyPaid());
        } else {
            summary.setAmountPaid(BigDecimal.ZERO);
            summary.setFullyPaid(false);
        }

        return summary;
    }
}

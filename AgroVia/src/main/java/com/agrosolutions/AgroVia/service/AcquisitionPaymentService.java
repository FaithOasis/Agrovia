package com.agrosolutions.AgroVia.service;

import com.agrosolutions.AgroVia.dto.*;
import com.agrosolutions.AgroVia.entity.*;
import com.agrosolutions.AgroVia.repository.*;

import com.agrosolutions.AgroVia.exception.BadRequestException;
import com.agrosolutions.AgroVia.exception.ResourceNotFoundException;
import com.agrosolutions.AgroVia.exception.UnauthorizedException;

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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AcquisitionPaymentService {
    @Autowired
    private AcquisitionPaymentRepository paymentRepository;

    @Autowired
    private AcquisitionParticipantRepository participantRepository;

    @Autowired
    private AcquisitionRepository acquisitionRepository;

    @Autowired
    private PaymentMilestoneRepository milestoneRepository;

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

    // Make a payment
    @Transactional
    public PaymentResponse makePayment(PaymentRequest request) {
        User currentUser = getCurrentUser();
        Acquisition acquisition = acquisitionRepository.findById(request.getAcquisitionId())
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition not found"));

        // Find participant
        AcquisitionParticipant participant;
        if (request.getParticipantId() != null) {
            participant = participantRepository.findById(request.getParticipantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));
        } else {
            participant = participantRepository.findByAcquisitionAndUser(acquisition, currentUser)
                    .orElseThrow(() -> new ResourceNotFoundException("You are not a participant in this acquisition"));
        }

        // Validate payment
        validatePayment(participant, request);

        // Create payment
        AcquisitionPayment payment = new AcquisitionPayment(
                participant,
                acquisition,
                request.getAmount(),
                AcquisitionPayment.PaymentMethod.valueOf(request.getPaymentMethod())
        );

        payment.setTransactionId(request.getTransactionId() != null ?
                request.getTransactionId() : generateTransactionId());
        payment.setNotes(request.getNotes());
        payment.setPaymentMilestone(request.getMilestoneNumber());

        // If milestone specified, link to it
        if (request.getMilestoneNumber() != null) {
            PaymentMilestone milestone = milestoneRepository
                    .findByAcquisitionAndMilestoneNumber(acquisition, request.getMilestoneNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));

            payment.setPaymentMilestone(milestone.getMilestoneNumber());

            // Update milestone
            milestone.addPayment(request.getAmount());
            milestoneRepository.save(milestone);
        }

        // For demo, auto-complete payment (in real app, integrate with payment gateway)
        payment.setStatus(AcquisitionPayment.PaymentStatus.COMPLETED);
        payment.setCompletedDate(LocalDateTime.now());

        AcquisitionPayment savedPayment = paymentRepository.save(payment);

        // Check if participant is now fully paid
        if (participant.isFullyPaid()) {
            participant.setStatus(AcquisitionParticipant.ParticipantStatus.PAID);
            participantRepository.save(participant);

            // Notify participant
            String message = "You have fully paid for \"" + acquisition.getTitle() + "\"";
            notificationService.createSystemAnnouncement(message, participant.getUser());

            // Notify creator
            String creatorMessage = participant.getUser().getUsername() +
                    " has fully paid for \"" + acquisition.getTitle() + "\"";
            notificationService.createSystemAnnouncement(creatorMessage, acquisition.getCreator());
        }

        return mapToPaymentResponse(savedPayment);
    }

    // Validate payment
    private void validatePayment(AcquisitionParticipant participant, PaymentRequest request) {
        // Check participant status
        if (participant.getStatus() != AcquisitionParticipant.ParticipantStatus.APPROVED &&
                participant.getStatus() != AcquisitionParticipant.ParticipantStatus.PAYING) {
            throw new BadRequestException("Participant is not in a valid state for payment");
        }

        // Check amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be greater than zero");
        }

        BigDecimal remainingAmount = participant.getContributionAmount().subtract(participant.getTotalPaid());
        if (request.getAmount().compareTo(remainingAmount) > 0) {
            throw new BadRequestException("Payment amount exceeds remaining balance. Remaining: " + remainingAmount);
        }

        // Check milestone if specified
        if (request.getMilestoneNumber() != null) {
            PaymentMilestone milestone = milestoneRepository
                    .findByAcquisitionAndMilestoneNumber(participant.getAcquisition(), request.getMilestoneNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));

            if (milestone.getStatus() == PaymentMilestone.MilestoneStatus.COMPLETED) {
                throw new BadRequestException("This milestone is already completed");
            }

            BigDecimal remainingForMilestone = milestone.getAmountDue().subtract(milestone.getAmountPaid());
            if (request.getAmount().compareTo(remainingForMilestone) > 0) {
                throw new BadRequestException("Amount exceeds remaining for this milestone");
            }
        }
    }

    // Generate transaction ID
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Get payments for an acquisition
    public Page<PaymentResponse> getAcquisitionPayments(Long acquisitionId, Pageable pageable) {
        Acquisition acquisition = acquisitionRepository.findById(acquisitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition not found"));

        User currentUser = getCurrentUser();
        // Only creator can see all payments
        if (!acquisition.getCreator().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the creator can view all payments");
        }

        return paymentRepository.findByAcquisition(acquisition, pageable)
                .map(this::mapToPaymentResponse);
    }

    // Get user's payment history
    public Page<PaymentResponse> getMyPayments(Pageable pageable) {
        User currentUser = getCurrentUser();
        // This is complex - you might need a custom query in repository
        // For now, return empty or implement later
        return Page.empty(pageable);
    }

    // Get payment by ID
    public PaymentResponse getPaymentById(Long id) {
        AcquisitionPayment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        User currentUser = getCurrentUser();
        // Check if user is participant or creator
        if (!payment.getParticipant().getUser().getId().equals(currentUser.getId()) &&
                !payment.getAcquisition().getCreator().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to view this payment");
        }

        return mapToPaymentResponse(payment);
    }

    // Create payment milestones for an acquisition
    @Transactional
    public void createPaymentMilestones(Acquisition acquisition, int numberOfMilestones,
                                        List<BigDecimal> percentages, List<LocalDateTime> dueDates) {
        if (percentages.size() != numberOfMilestones || dueDates.size() != numberOfMilestones) {
            throw new BadRequestException("Milestones data mismatch");
        }

        BigDecimal totalPercentage = percentages.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPercentage.compareTo(new BigDecimal(100)) != 0) {
            throw new BadRequestException("Total percentage must be 100%");
        }

        for (int i = 0; i < numberOfMilestones; i++) {
            PaymentMilestone milestone = new PaymentMilestone(
                    acquisition,
                    i + 1,
                    "Milestone " + (i + 1),
                    acquisition.getTotalPrice().multiply(percentages.get(i).divide(new BigDecimal(100))),
                    dueDates.get(i)
            );
            milestone.setPercentage(percentages.get(i).intValue());
            milestone.setDescription("Payment " + (i + 1) + " of " + numberOfMilestones);

            milestoneRepository.save(milestone);
        }
    }

    // Get payment milestones for an acquisition
    public List<PaymentMilestoneResponse> getPaymentMilestones(Long acquisitionId) {
        Acquisition acquisition = acquisitionRepository.findById(acquisitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition not found"));

        return milestoneRepository.findByAcquisitionOrderByMilestoneNumberAsc(acquisition)
                .stream()
                .map(this::mapToMilestoneResponse)
                .collect(Collectors.toList());
    }

    // Map Payment to PaymentResponse
    private PaymentResponse mapToPaymentResponse(AcquisitionPayment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setAcquisitionId(payment.getAcquisition().getId());
        response.setAcquisitionTitle(payment.getAcquisition().getTitle());
        response.setAmount(payment.getAmount());
        response.setPaymentDate(payment.getPaymentDate());
        response.setPaymentMethod(payment.getPaymentMethod().toString());
        response.setTransactionId(payment.getTransactionId());
        response.setStatus(payment.getStatus().toString());
        response.setMilestoneNumber(payment.getPaymentMilestone());
        response.setDueDate(payment.getDueDate());
        response.setCompletedDate(payment.getCompletedDate());
        response.setReceiptUrl(payment.getReceiptUrl());

        // Map participant
        if (payment.getParticipant() != null) {
            ParticipantSummary participantSummary = new ParticipantSummary();
            participantSummary.setId(payment.getParticipant().getId());
            participantSummary.setQuantity(payment.getParticipant().getQuantity());

            if (payment.getParticipant().getUser() != null) {
                UserSummary userSummary = new UserSummary(
                        payment.getParticipant().getUser().getId(),
                        payment.getParticipant().getUser().getUsername(),
                        payment.getParticipant().getUser().getEmail(),
                        payment.getParticipant().getUser().getFullName()
                );
                participantSummary.setUser(userSummary);
            }

            response.setParticipant(participantSummary);
        }

        return response;
    }

    // Map Milestone to MilestoneResponse
    private PaymentMilestoneResponse mapToMilestoneResponse(PaymentMilestone milestone) {
        PaymentMilestoneResponse response = new PaymentMilestoneResponse();
        response.setId(milestone.getId());
        response.setMilestoneNumber(milestone.getMilestoneNumber());
        response.setTitle(milestone.getTitle());
        response.setDescription(milestone.getDescription());
        response.setDueDate(milestone.getDueDate());
        response.setAmountDue(milestone.getAmountDue());
        response.setAmountPaid(milestone.getAmountPaid());
        response.setPercentage(milestone.getPercentage());
        response.setStatus(milestone.getStatus().toString());
        response.setCompletedDate(milestone.getCompletedDate());
        response.setMandatory(milestone.getIsMandatory() != null ? milestone.getIsMandatory() : true);
        response.setOverdue(milestone.isDue());

        return response;
    }
}

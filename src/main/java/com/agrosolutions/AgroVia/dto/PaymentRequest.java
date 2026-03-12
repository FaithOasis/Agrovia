package com.agrosolutions.AgroVia.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    private Long acquisitionId;
    private Long participantId; // Optional, if paying for specific participant
    private BigDecimal amount;
    private String paymentMethod;
    private Integer milestoneNumber; // Optional, if paying for specific milestone
    private String transactionId;
    private String notes;

    // Default constructor
    public PaymentRequest() {
    }

    // Getters and Setters
    public Long getAcquisitionId() {
        return acquisitionId;
    }

    public void setAcquisitionId(Long acquisitionId) {
        this.acquisitionId = acquisitionId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getMilestoneNumber() {
        return milestoneNumber;
    }

    public void setMilestoneNumber(Integer milestoneNumber) {
        this.milestoneNumber = milestoneNumber;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

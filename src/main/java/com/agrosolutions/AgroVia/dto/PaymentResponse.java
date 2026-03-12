package com.agrosolutions.AgroVia.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {
    private Long id;
    private Long acquisitionId;
    private String acquisitionTitle;
    private ParticipantSummary participant;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String transactionId;
    private String status;
    private Integer milestoneNumber;
    private LocalDateTime dueDate;
    private LocalDateTime completedDate;
    private String receiptUrl;

    // Default constructor
    public PaymentResponse() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAcquisitionId() {
        return acquisitionId;
    }

    public void setAcquisitionId(Long acquisitionId) {
        this.acquisitionId = acquisitionId;
    }

    public String getAcquisitionTitle() {
        return acquisitionTitle;
    }

    public void setAcquisitionTitle(String acquisitionTitle) {
        this.acquisitionTitle = acquisitionTitle;
    }

    public ParticipantSummary getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantSummary participant) {
        this.participant = participant;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMilestoneNumber() {
        return milestoneNumber;
    }

    public void setMilestoneNumber(Integer milestoneNumber) {
        this.milestoneNumber = milestoneNumber;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }
}
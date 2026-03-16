package com.agrosolutions.AgroVia.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public class AcquisitionResponse {
    private Long id;
    private String title;
    private String description;
    private String type;
    private UserSummary creator;
    private ProductSummary product;
    private Integer targetQuantity;
    private Integer currentQuantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Integer minContribution;
    private Integer maxContribution;
    private Integer minParticipants;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime deliveryDate;
    private String status;
    private String location;
    private String termsConditions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ParticipantSummary> participants;
    private List<PaymentMilestoneResponse> paymentMilestones;
    private Double progressPercentage;

    // Default constructor
    public AcquisitionResponse() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserSummary getCreator() {
        return creator;
    }

    public void setCreator(UserSummary creator) {
        this.creator = creator;
    }

    public ProductSummary getProduct() {
        return product;
    }

    public void setProduct(ProductSummary product) {
        this.product = product;
    }

    public Integer getTargetQuantity() {
        return targetQuantity;
    }

    public void setTargetQuantity(Integer targetQuantity) {
        this.targetQuantity = targetQuantity;
    }

    public Integer getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(Integer currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getMinContribution() {
        return minContribution;
    }

    public void setMinContribution(Integer minContribution) {
        this.minContribution = minContribution;
    }

    public Integer getMaxContribution() {
        return maxContribution;
    }

    public void setMaxContribution(Integer maxContribution) {
        this.maxContribution = maxContribution;
    }

    public Integer getMinParticipants() {
        return minParticipants;
    }

    public void setMinParticipants(Integer minParticipants) {
        this.minParticipants = minParticipants;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Integer getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTermsConditions() {
        return termsConditions;
    }

    public void setTermsConditions(String termsConditions) {
        this.termsConditions = termsConditions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ParticipantSummary> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantSummary> participants) {
        this.participants = participants;
    }

    public List<PaymentMilestoneResponse> getPaymentMilestones() {
        return paymentMilestones;
    }

    public void setPaymentMilestones(List<PaymentMilestoneResponse> paymentMilestones) {
        this.paymentMilestones = paymentMilestones;
    }

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
}

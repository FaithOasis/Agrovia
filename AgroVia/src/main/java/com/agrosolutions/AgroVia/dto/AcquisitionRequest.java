package com.agrosolutions.AgroVia.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class AcquisitionRequest {
    private String title;
    private String description;
    private String type; // BUYING or SELLING
    private Long productId;
    private Integer targetQuantity;
    private BigDecimal unitPrice;
    private Integer minContribution;
    private Integer maxContribution;
    private Integer minParticipants;
    private Integer maxParticipants;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime deliveryDate;
    private String location;
    private String termsConditions;

    // Default constructor
    public AcquisitionRequest() {
    }

    // Getters and Setters
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getTargetQuantity() {
        return targetQuantity;
    }

    public void setTargetQuantity(Integer targetQuantity) {
        this.targetQuantity = targetQuantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
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
}

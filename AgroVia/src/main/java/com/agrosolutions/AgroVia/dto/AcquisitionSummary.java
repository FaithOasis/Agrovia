package com.agrosolutions.AgroVia.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class AcquisitionSummary {
    private Long id;
    private String title;
    private String type;
    private UserSummary creator;
    private Integer targetQuantity;
    private Integer currentQuantity;
    private BigDecimal unitPrice;
    private String status;
    private LocalDateTime endDate;
    private Integer participantCount;
    private Double progressPercentage;

    // Default constructor
    public AcquisitionSummary() {
    }

    // Constructor with fields
    public AcquisitionSummary(Long id, String title, String type, UserSummary creator,
                              Integer targetQuantity, Integer currentQuantity,
                              BigDecimal unitPrice, String status, LocalDateTime endDate,
                              Integer participantCount, Double progressPercentage) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.creator = creator;
        this.targetQuantity = targetQuantity;
        this.currentQuantity = currentQuantity;
        this.unitPrice = unitPrice;
        this.status = status;
        this.endDate = endDate;
        this.participantCount = participantCount;
        this.progressPercentage = progressPercentage;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

}

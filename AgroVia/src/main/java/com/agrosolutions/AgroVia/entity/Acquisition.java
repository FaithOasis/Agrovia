package com.agrosolutions.AgroVia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "acquisitions")
public class Acquisition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcquisitionType type; // BUYING or SELLING

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "target_quantity", nullable = false)
    private Integer targetQuantity;

    @Column(name = "current_quantity")
    private Integer currentQuantity = 0;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "min_contribution")
    private Integer minContribution; // Minimum quantity per participant

    @Column(name = "max_contribution")
    private Integer maxContribution; // Maximum quantity per participant

    @Column(name = "min_participants")
    private Integer minParticipants = 1;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcquisitionStatus status = AcquisitionStatus.DRAFT;

    @Column(name = "location", length = 255)
    private String location; // Delivery/pickup location

    @Column(name = "terms_conditions", length = 2000)
    private String termsConditions;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "acquisition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AcquisitionParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "acquisition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentMilestone> paymentMilestones = new ArrayList<>();

    @OneToMany(mappedBy = "acquisition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryTracking> deliveryTracking = new ArrayList<>();

    // Default constructor
    public Acquisition() {
    }

    // Constructor with main fields
    public Acquisition(String title, AcquisitionType type, User creator,
                       Integer targetQuantity, BigDecimal unitPrice,
                       LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.type = type;
        this.creator = creator;
        this.targetQuantity = targetQuantity;
        this.unitPrice = unitPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = AcquisitionStatus.DRAFT;
        this.currentQuantity = 0;
        calculateTotalPrice();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotalPrice();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        if (unitPrice != null && targetQuantity != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(targetQuantity));
        }
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

    public AcquisitionType getType() {
        return type;
    }

    public void setType(AcquisitionType type) {
        this.type = type;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getTargetQuantity() {
        return targetQuantity;
    }

    public void setTargetQuantity(Integer targetQuantity) {
        this.targetQuantity = targetQuantity;
        calculateTotalPrice();
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
        calculateTotalPrice();
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

    public AcquisitionStatus getStatus() {
        return status;
    }

    public void setStatus(AcquisitionStatus status) {
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

    public List<AcquisitionParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<AcquisitionParticipant> participants) {
        this.participants = participants;
    }

    public List<PaymentMilestone> getPaymentMilestones() {
        return paymentMilestones;
    }

    public void setPaymentMilestones(List<PaymentMilestone> paymentMilestones) {
        this.paymentMilestones = paymentMilestones;
    }

    public List<DeliveryTracking> getDeliveryTracking() {
        return deliveryTracking;
    }

    public void setDeliveryTracking(List<DeliveryTracking> deliveryTracking) {
        this.deliveryTracking = deliveryTracking;
    }

    // Helper methods
    public void addParticipant(AcquisitionParticipant participant) {
        participants.add(participant);
        participant.setAcquisition(this);
        this.currentQuantity += participant.getQuantity();
    }

    public void removeParticipant(AcquisitionParticipant participant) {
        participants.remove(participant);
        participant.setAcquisition(null);
        this.currentQuantity -= participant.getQuantity();
    }

    public void addPaymentMilestone(PaymentMilestone milestone) {
        paymentMilestones.add(milestone);
        milestone.setAcquisition(this);
    }

    public void addDeliveryTracking(DeliveryTracking tracking) {
        deliveryTracking.add(tracking);
        tracking.setAcquisition(this);
    }

    // Check if target is reached
    public boolean isTargetReached() {
        return currentQuantity >= targetQuantity;
    }

    // Check if acquisition is open for joining
    public boolean isOpen() {
        return status == AcquisitionStatus.OPEN &&
                LocalDateTime.now().isBefore(endDate) &&
                !isTargetReached() &&
                (maxParticipants == null || participants.size() < maxParticipants);
    }

    // Enums
    public enum AcquisitionType {
        BUYING, SELLING
    }

    public enum AcquisitionStatus {
        DRAFT, OPEN, CLOSED, FUNDED, DELIVERED, COMPLETED, CANCELLED
    }
}

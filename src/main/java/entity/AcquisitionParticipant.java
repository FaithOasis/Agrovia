package entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "acquisition_participants")
public class AcquisitionParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquisition_id", nullable = false)
    private Acquisition acquisition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "contribution_amount", precision = 10, scale = 2)
    private BigDecimal contributionAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantStatus status = ParticipantStatus.PENDING;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "notes", length = 500)
    private String notes;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<AcquisitionPayment> payments;

    // Default constructor
    public AcquisitionParticipant() {
    }

    // Constructor with main fields
    public AcquisitionParticipant(Acquisition acquisition, User user, Integer quantity) {
        this.acquisition = acquisition;
        this.user = user;
        this.quantity = quantity;
        this.status = ParticipantStatus.PENDING;
        calculateContributionAmount();
    }

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
        calculateContributionAmount();
    }

    private void calculateContributionAmount() {
        if (acquisition != null && acquisition.getUnitPrice() != null && quantity != null) {
            this.contributionAmount = acquisition.getUnitPrice()
                    .multiply(BigDecimal.valueOf(quantity));
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Acquisition getAcquisition() {
        return acquisition;
    }

    public void setAcquisition(Acquisition acquisition) {
        this.acquisition = acquisition;
        calculateContributionAmount();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateContributionAmount();
    }

    public BigDecimal getContributionAmount() {
        return contributionAmount;
    }

    public void setContributionAmount(BigDecimal contributionAmount) {
        this.contributionAmount = contributionAmount;
    }

    public ParticipantStatus getStatus() {
        return status;
    }

    public void setStatus(ParticipantStatus status) {
        this.status = status;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<AcquisitionPayment> getPayments() {
        return payments;
    }

    public void setPayments(List<AcquisitionPayment> payments) {
        this.payments = payments;
    }

    // Calculate total paid amount - FIXED
    public BigDecimal getTotalPaid() {
        if (payments == null || payments.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (AcquisitionPayment payment : payments) {
            if (payment != null && payment.getStatus() == AcquisitionPayment.PaymentStatus.COMPLETED) {
                total = total.add(payment.getAmount());
            }
        }
        return total;
    }

    // Check if fully paid
    public boolean isFullyPaid() {
        return getTotalPaid().compareTo(contributionAmount) >= 0;
    }

    // Enums
    public enum ParticipantStatus {
        PENDING, APPROVED, REJECTED, WITHDRAWN, PAYING, PAID, RECEIVED
    }
}
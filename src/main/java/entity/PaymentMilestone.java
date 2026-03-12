package entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_milestones")
public class PaymentMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquisition_id", nullable = false)
    private Acquisition acquisition;

    @Column(name = "milestone_number", nullable = false)
    private Integer milestoneNumber;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "amount_due", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountDue;

    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "percentage")
    private Integer percentage; // Percentage of total payment

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MilestoneStatus status = MilestoneStatus.PENDING;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "is_mandatory")
    private Boolean isMandatory = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor
    public PaymentMilestone() {
    }

    // Constructor with main fields
    public PaymentMilestone(Acquisition acquisition, Integer milestoneNumber,
                            String title, BigDecimal amountDue, LocalDateTime dueDate) {
        this.acquisition = acquisition;
        this.milestoneNumber = milestoneNumber;
        this.title = title;
        this.amountDue = amountDue;
        this.dueDate = dueDate;
        this.status = MilestoneStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
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
    }

    public Integer getMilestoneNumber() {
        return milestoneNumber;
    }

    public void setMilestoneNumber(Integer milestoneNumber) {
        this.milestoneNumber = milestoneNumber;
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

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public MilestoneStatus getStatus() {
        return status;
    }

    public void setStatus(MilestoneStatus status) {
        this.status = status;
        if (status == MilestoneStatus.COMPLETED) {
            this.completedDate = LocalDateTime.now();
        }
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Check if milestone is due
    public boolean isDue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate) &&
                status == MilestoneStatus.PENDING;
    }

    // Check if fully paid
    public boolean isFullyPaid() {
        return amountPaid.compareTo(amountDue) >= 0;
    }

    // Add payment to this milestone
    public void addPayment(BigDecimal amount) {
        this.amountPaid = this.amountPaid.add(amount);
        if (isFullyPaid()) {
            this.status = MilestoneStatus.COMPLETED;
        }
    }

    // Enums
    public enum MilestoneStatus {
        PENDING, IN_PROGRESS, COMPLETED, SKIPPED, OVERDUE
    }
}
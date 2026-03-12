package entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "acquisition_payments")
public class AcquisitionPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private AcquisitionParticipant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquisition_id", nullable = false)
    private Acquisition acquisition;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_id", unique = true, length = 100)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_milestone")
    private Integer paymentMilestone; // 1st, 2nd, 3rd etc.

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(length = 500)
    private String notes;

    @Column(name = "receipt_url", length = 500)
    private String receiptUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor
    public AcquisitionPayment() {
    }

    // Constructor with main fields
    public AcquisitionPayment(AcquisitionParticipant participant, Acquisition acquisition,
                              BigDecimal amount, PaymentMethod paymentMethod) {
        this.participant = participant;
        this.acquisition = acquisition;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AcquisitionParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(AcquisitionParticipant participant) {
        this.participant = participant;
    }

    public Acquisition getAcquisition() {
        return acquisition;
    }

    public void setAcquisition(Acquisition acquisition) {
        this.acquisition = acquisition;
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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
        if (status == PaymentStatus.COMPLETED) {
            this.completedDate = LocalDateTime.now();
        }
    }

    public Integer getPaymentMilestone() {
        return paymentMilestone;
    }

    public void setPaymentMilestone(Integer paymentMilestone) {
        this.paymentMilestone = paymentMilestone;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Enums
    public enum PaymentMethod {
        CASH, BANK_TRANSFER, MOBILE_MONEY, CREDIT_CARD, INSTALLMENT
    }

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, REFUNDED, PARTIAL
    }
}

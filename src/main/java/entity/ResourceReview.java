package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_reviews")
public class ResourceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private ResourceBooking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Column(nullable = false)
    private Integer rating; // 1-5

    @Column(length = 1000)
    private String comment;

    @Column(name = "owner_response", length = 1000)
    private String ownerResponse;

    @Column(name = "response_created_at")
    private LocalDateTime responseCreatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public ResourceReview() {
    }

    // Constructor with fields
    public ResourceReview(ResourceBooking booking, Resource resource, User reviewer,
                          Integer rating, String comment) {
        this.booking = booking;
        this.resource = resource;
        this.reviewer = reviewer;
        this.rating = rating;
        this.comment = comment;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResourceBooking getBooking() {
        return booking;
    }

    public void setBooking(ResourceBooking booking) {
        this.booking = booking;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOwnerResponse() {
        return ownerResponse;
    }

    public void setOwnerResponse(String ownerResponse) {
        this.ownerResponse = ownerResponse;
    }

    public LocalDateTime getResponseCreatedAt() {
        return responseCreatedAt;
    }

    public void setResponseCreatedAt(LocalDateTime responseCreatedAt) {
        this.responseCreatedAt = responseCreatedAt;
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
}

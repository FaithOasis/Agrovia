package com.agrosolutions.AgroVia.dto;

import java.time.LocalDateTime;

public class ResourceReviewResponse {
    private Long id;
    private UserSummary reviewer;
    private Integer rating;
    private String comment;
    private String ownerResponse;
    private LocalDateTime responseCreatedAt;
    private LocalDateTime createdAt;

    // Default constructor
    public ResourceReviewResponse() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserSummary getReviewer() {
        return reviewer;
    }

    public void setReviewer(UserSummary reviewer) {
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
}
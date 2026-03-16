package com.agrosolutions.AgroVia.dto;
import java.math.BigDecimal;
public class ResourceSummary {
    private Long id;
    private String title;
    private UserSummary owner;
    private CategorySummary category;
    private BigDecimal pricePerDay;
    private String location;
    private String status;
    private String primaryImageUrl;
    private Double averageRating;
    private Integer reviewCount;

    // Default constructor
    public ResourceSummary() {
    }

    // Constructor with fields
    public ResourceSummary(Long id, String title, UserSummary owner, CategorySummary category,
                           BigDecimal pricePerDay, String location, String status,
                           String primaryImageUrl, Double averageRating, Integer reviewCount) {
        this.id = id;
        this.title = title;
        this.owner = owner;
        this.category = category;
        this.pricePerDay = pricePerDay;
        this.location = location;
        this.status = status;
        this.primaryImageUrl = primaryImageUrl;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
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

    public UserSummary getOwner() {
        return owner;
    }

    public void setOwner(UserSummary owner) {
        this.owner = owner;
    }

    public CategorySummary getCategory() {
        return category;
    }

    public void setCategory(CategorySummary category) {
        this.category = category;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

}

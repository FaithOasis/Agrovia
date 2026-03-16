package com.agrosolutions.AgroVia.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public class ResourceResponse {
    private Long id;
    private String title;
    private String description;
    private UserSummary owner;
    private CategorySummary category;
    private BigDecimal pricePerHour;
    private BigDecimal pricePerDay;
    private BigDecimal pricePerWeek;
    private BigDecimal depositAmount;
    private String location;
    private Double latitude;
    private Double longitude;
    private Integer deliveryRadius;
    private String brand;
    private String model;
    private Integer year;
    private String condition;
    private Integer quantityAvailable;
    private Integer minRentalPeriod;
    private Integer maxRentalPeriod;
    private Integer advanceNotice;
    private String status;
    private Integer viewsCount;
    private Integer bookingsCount;
    private Double averageRating;
    private Integer reviewCount;
    private List<ResourceImageResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public ResourceResponse() {
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

    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(BigDecimal pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public BigDecimal getPricePerWeek() {
        return pricePerWeek;
    }

    public void setPricePerWeek(BigDecimal pricePerWeek) {
        this.pricePerWeek = pricePerWeek;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getDeliveryRadius() {
        return deliveryRadius;
    }

    public void setDeliveryRadius(Integer deliveryRadius) {
        this.deliveryRadius = deliveryRadius;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public Integer getMinRentalPeriod() {
        return minRentalPeriod;
    }

    public void setMinRentalPeriod(Integer minRentalPeriod) {
        this.minRentalPeriod = minRentalPeriod;
    }

    public Integer getMaxRentalPeriod() {
        return maxRentalPeriod;
    }

    public void setMaxRentalPeriod(Integer maxRentalPeriod) {
        this.maxRentalPeriod = maxRentalPeriod;
    }

    public Integer getAdvanceNotice() {
        return advanceNotice;
    }

    public void setAdvanceNotice(Integer advanceNotice) {
        this.advanceNotice = advanceNotice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(Integer viewsCount) {
        this.viewsCount = viewsCount;
    }

    public Integer getBookingsCount() {
        return bookingsCount;
    }

    public void setBookingsCount(Integer bookingsCount) {
        this.bookingsCount = bookingsCount;
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

    public List<ResourceImageResponse> getImages() {
        return images;
    }

    public void setImages(List<ResourceImageResponse> images) {
        this.images = images;
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

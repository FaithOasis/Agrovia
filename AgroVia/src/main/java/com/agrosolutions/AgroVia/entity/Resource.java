package com.agrosolutions.AgroVia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resource")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ResourceCategory category;

    @Column(name = "price_per_hour", precision = 10, scale = 2)
    private BigDecimal pricePerHour;

    @Column(name = "price_per_day", precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    @Column(name = "price_per_week", precision = 10, scale = 2)
    private BigDecimal pricePerWeek;

    @Column(name = "deposit_amount", precision = 10, scale = 2)
    private BigDecimal depositAmount;

    @Column(length = 255)
    private String location;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "delivery_radius")
    private Integer deliveryRadius; // in kilometers

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String model;

    private Integer year;

    @Column(name = "",length = 50)
    private String equipment_condition; // NEW, LIKE_NEW, GOOD, FAIR, POOR

    @Column(name = "quantity_available")
    private Integer quantityAvailable = 1;

    @Column(name = "min_rental_period")
    private Integer minRentalPeriod; // in hours

    @Column(name = "max_rental_period")
    private Integer maxRentalPeriod; // in hours

    @Column(name = "advance_notice")
    private Integer advanceNotice; // in hours

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status = ResourceStatus.AVAILABLE;

    @Column(name = "views_count")
    private Integer viewsCount = 0;

    @Column(name = "bookings_count")
    private Integer bookingsCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ResourceImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceAvailability> availability = new ArrayList<>();

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    private List<ResourceBooking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    private List<ResourceReview> reviews = new ArrayList<>();

    // Default constructor
    public Resource() {
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public ResourceCategory getCategory() {
        return category;
    }

    public void setCategory(ResourceCategory category) {
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

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
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
        return equipment_condition;
    }

    public void setCondition(String condition) {
        this.equipment_condition = condition;
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

    public ResourceStatus getStatus() {
        return status;
    }

    public void setStatus(ResourceStatus status) {
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

    public List<ResourceImage> getImages() {
        return images;
    }

    public void setImages(List<ResourceImage> images) {
        this.images = images;
    }

    public List<ResourceAvailability> getAvailability() {
        return availability;
    }

    public void setAvailability(List<ResourceAvailability> availability) {
        this.availability = availability;
    }

    public List<ResourceBooking> getBookings() {
        return bookings;
    }

    public void setBookings(List<ResourceBooking> bookings) {
        this.bookings = bookings;
    }

    public List<ResourceReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<ResourceReview> reviews) {
        this.reviews = reviews;
    }

    // Helper methods
    public void addImage(ResourceImage image) {
        images.add(image);
        image.setResource(this);
    }

    public void removeImage(ResourceImage image) {
        images.remove(image);
        image.setResource(null);
    }

    public void addAvailability(ResourceAvailability availabilitySlot) {
        availability.add(availabilitySlot);
        availabilitySlot.setResource(this);
    }

    public ResourceImage getPrimaryImage() {
        return images.stream()
                .filter(ResourceImage::isPrimary)
                .findFirst()
                .orElse(images.isEmpty() ? null : images.get(0));
    }

    // Calculate average rating
    public Double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(ResourceReview::getRating)
                .average()
                .orElse(0.0);
    }

    // Get total review count
    public int getReviewCount() {
        return reviews != null ? reviews.size() : 0;
    }

    // Check if resource is available for date range
    public boolean isAvailableForDates(LocalDateTime start, LocalDateTime end) {
        // This will be implemented in service layer with complex logic
        return true;
    }

    // Enums
    public enum ResourceStatus {
        AVAILABLE, UNAVAILABLE, MAINTENANCE, DELETED
    }
}

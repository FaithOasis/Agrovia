package com.agrosolutions.AgroVia.dto;
import java.math.BigDecimal;
public class ResourceRequest {
    private String title;
    private String description;
    private Long categoryId;
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

    // Default constructor
    public ResourceRequest() {
    }

    // Getters and Setters
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

}

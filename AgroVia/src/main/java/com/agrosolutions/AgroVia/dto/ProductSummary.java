package com.agrosolutions.AgroVia.dto;

import java.math.BigDecimal;

public class ProductSummary {
    private Long id;
    private String title;
    private BigDecimal price;
    private String unit;
    private String imageUrl;

    // Default constructor
    public ProductSummary() {
    }

    // Constructor with fields
    public ProductSummary(Long id, String title, BigDecimal price, String unit, String imageUrl) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.unit = unit;
        this.imageUrl = imageUrl;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
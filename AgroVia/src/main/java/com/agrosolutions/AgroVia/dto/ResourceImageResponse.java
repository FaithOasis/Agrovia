package com.agrosolutions.AgroVia.dto;

public class ResourceImageResponse {
    private Long id;
    private String imageUrl;
    private boolean isPrimary;
    private Integer sortOrder;

    // Default constructor
    public ResourceImageResponse() {
    }

    // Constructor with fields
    public ResourceImageResponse(Long id, String imageUrl, boolean isPrimary, Integer sortOrder) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary;
        this.sortOrder = sortOrder;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

}

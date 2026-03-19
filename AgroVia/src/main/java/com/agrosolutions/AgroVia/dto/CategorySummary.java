package com.agrosolutions.AgroVia.dto;

public class CategorySummary {
    private Long id;
    private String name;
    private String icon;
    private Long resourceCount;

    // Default constructor
    public CategorySummary() {
    }

    // Constructor with id and name only (for basic category info)
    public CategorySummary(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Constructor with all fields
    public CategorySummary(Long id, String name, String icon, Long resourceCount) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.resourceCount = resourceCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(Long resourceCount) {
        this.resourceCount = resourceCount;
    }

}
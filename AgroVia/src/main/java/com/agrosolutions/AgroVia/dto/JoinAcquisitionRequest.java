package com.agrosolutions.AgroVia.dto;

public class JoinAcquisitionRequest {
    private Long acquisitionId;
    private Integer quantity;
    private String notes;

    // Default constructor
    public JoinAcquisitionRequest() {
    }

    // Getters and Setters
    public Long getAcquisitionId() {
        return acquisitionId;
    }

    public void setAcquisitionId(Long acquisitionId) {
        this.acquisitionId = acquisitionId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

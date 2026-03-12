package com.agrosolutions.AgroVia.dto;

import java.math.BigDecimal;

public class OrderRequest {
    private Long productId;
    private Long negotiationId;
    private Integer quantity;
    private String shippingAddress;
    private String phoneNumber;
    private String paymentMethod;
    private String notes;

    // Default constructor
    public OrderRequest() {
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getNegotiationId() {
        return negotiationId;
    }

    public void setNegotiationId(Long negotiationId) {
        this.negotiationId = negotiationId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
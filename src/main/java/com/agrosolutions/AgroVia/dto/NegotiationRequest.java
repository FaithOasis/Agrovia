package com.agrosolutions.AgroVia.dto;

import java.math.BigDecimal;

public class NegotiationRequest {
    private Long productId;
    private BigDecimal offeredPrice;
    private Integer quantity;
    private String message;

    // Default constructor
    public NegotiationRequest() {
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getOfferedPrice() {
        return offeredPrice;
    }

    public void setOfferedPrice(BigDecimal offeredPrice) {
        this.offeredPrice = offeredPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
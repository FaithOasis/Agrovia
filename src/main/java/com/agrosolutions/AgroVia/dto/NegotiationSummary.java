package com.agrosolutions.AgroVia.dto;

import java.math.BigDecimal;

public class NegotiationSummary {
    private Long id;
    private BigDecimal offeredPrice;
    private String status;

    // Default constructor
    public NegotiationSummary() {
    }

    // Constructor with fields
    public NegotiationSummary(Long id, BigDecimal offeredPrice, String status) {
        this.id = id;
        this.offeredPrice = offeredPrice;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getOfferedPrice() {
        return offeredPrice;
    }

    public void setOfferedPrice(BigDecimal offeredPrice) {
        this.offeredPrice = offeredPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
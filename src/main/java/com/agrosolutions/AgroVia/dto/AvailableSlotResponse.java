package com.agrosolutions.AgroVia.dto;

import java.time.LocalDateTime;

public class AvailableSlotResponse {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private boolean available;

    // Default constructor
    public AvailableSlotResponse() {
    }

    // Constructor with fields
    public AvailableSlotResponse(LocalDateTime startDateTime, LocalDateTime endDateTime, boolean available) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.available = available;
    }

    // Getters and Setters
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}

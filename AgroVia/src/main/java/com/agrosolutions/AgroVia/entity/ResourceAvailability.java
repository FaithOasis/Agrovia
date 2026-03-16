package com.agrosolutions.AgroVia.entity;
import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "resource_availability")
public class ResourceAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_type", nullable = false)
    private AvailabilityType availabilityType; // RECURRING or CUSTOM

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek; // For recurring

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "start_date")
    private LocalDate startDate; // For custom range

    @Column(name = "end_date")
    private LocalDate endDate; // For custom range

    @Column(name = "is_available")
    private boolean isAvailable = true;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor
    public ResourceAvailability() {
    }

    // Constructor for recurring availability
    public ResourceAvailability(Resource resource, DayOfWeek dayOfWeek,
                                LocalTime startTime, LocalTime endTime) {
        this.resource = resource;
        this.availabilityType = AvailabilityType.RECURRING;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = true;
    }

    // Constructor for custom date range
    public ResourceAvailability(Resource resource, LocalDate startDate, LocalDate endDate,
                                LocalTime startTime, LocalTime endTime) {
        this.resource = resource;
        this.availabilityType = AvailabilityType.CUSTOM;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = true;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Check if a specific datetime is within this availability slot
    public boolean covers(LocalDateTime dateTime) {
        if (availabilityType == AvailabilityType.RECURRING) {
            // Check day of week and time
            return dateTime.getDayOfWeek() == dayOfWeek &&
                    !dateTime.toLocalTime().isBefore(startTime) &&
                    !dateTime.toLocalTime().isAfter(endTime);
        } else {
            // Check date range and time
            LocalDate date = dateTime.toLocalDate();
            return !date.isBefore(startDate) &&
                    !date.isAfter(endDate) &&
                    !dateTime.toLocalTime().isBefore(startTime) &&
                    !dateTime.toLocalTime().isAfter(endTime);
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public AvailabilityType getAvailabilityType() {
        return availabilityType;
    }

    public void setAvailabilityType(AvailabilityType availabilityType) {
        this.availabilityType = availabilityType;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Enums
    public enum AvailabilityType {
        RECURRING, CUSTOM
    }
}

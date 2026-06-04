package com.staynest.booking.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AvailabilityRequest {

    @NotNull(message = "Available from date is required")
    private LocalDate availableFrom;

    @NotNull(message = "Available to date is required")
    private LocalDate availableTo;

    public AvailabilityRequest() {}

    public AvailabilityRequest(LocalDate availableFrom, LocalDate availableTo) {
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
    }

    public LocalDate getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }

    public LocalDate getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(LocalDate availableTo) {
        this.availableTo = availableTo;
    }
}

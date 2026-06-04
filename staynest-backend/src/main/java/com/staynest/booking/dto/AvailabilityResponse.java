package com.staynest.booking.dto;

import java.time.LocalDate;

public class AvailabilityResponse {
    private Long id;
    private Long propertyId;
    private LocalDate availableFrom;
    private LocalDate availableTo;

    public AvailabilityResponse() {}

    public AvailabilityResponse(Long id, Long propertyId, LocalDate availableFrom, LocalDate availableTo) {
        this.id = id;
        this.propertyId = propertyId;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
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

    public static AvailabilityResponseBuilder builder() {
        return new AvailabilityResponseBuilder();
    }

    public static class AvailabilityResponseBuilder {
        private Long id;
        private Long propertyId;
        private LocalDate availableFrom;
        private LocalDate availableTo;

        public AvailabilityResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AvailabilityResponseBuilder propertyId(Long propertyId) {
            this.propertyId = propertyId;
            return this;
        }

        public AvailabilityResponseBuilder availableFrom(LocalDate availableFrom) {
            this.availableFrom = availableFrom;
            return this;
        }

        public AvailabilityResponseBuilder availableTo(LocalDate availableTo) {
            this.availableTo = availableTo;
            return this;
        }

        public AvailabilityResponse build() {
            return new AvailabilityResponse(id, propertyId, availableFrom, availableTo);
        }
    }
}

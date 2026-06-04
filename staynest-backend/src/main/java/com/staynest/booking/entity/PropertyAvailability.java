package com.staynest.booking.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "property_availabilities", indexes = {
    @Index(name = "idx_availabilities_dates", columnList = "property_id, available_from, available_to")
})
public class PropertyAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "available_from", nullable = false)
    private LocalDate availableFrom;

    @Column(name = "available_to", nullable = false)
    private LocalDate availableTo;

    public PropertyAvailability() {}

    public PropertyAvailability(Long id, Property property, LocalDate availableFrom, LocalDate availableTo) {
        this.id = id;
        this.property = property;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
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

    public static PropertyAvailabilityBuilder builder() {
        return new PropertyAvailabilityBuilder();
    }

    public static class PropertyAvailabilityBuilder {
        private Long id;
        private Property property;
        private LocalDate availableFrom;
        private LocalDate availableTo;

        public PropertyAvailabilityBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PropertyAvailabilityBuilder property(Property property) {
            this.property = property;
            return this;
        }

        public PropertyAvailabilityBuilder availableFrom(LocalDate availableFrom) {
            this.availableFrom = availableFrom;
            return this;
        }

        public PropertyAvailabilityBuilder availableTo(LocalDate availableTo) {
            this.availableTo = availableTo;
            return this;
        }

        public PropertyAvailability build() {
            return new PropertyAvailability(id, property, availableFrom, availableTo);
        }
    }
}

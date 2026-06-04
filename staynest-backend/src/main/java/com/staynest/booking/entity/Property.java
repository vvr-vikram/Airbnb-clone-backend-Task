package com.staynest.booking.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "properties", indexes = {
    @Index(name = "idx_properties_location", columnList = "location"),
    @Index(name = "idx_properties_price", columnList = "price_per_night")
})
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "location", nullable = false, length = 100)
    private String location;

    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Property() {}

    public Property(Long id, String title, String description, String location, BigDecimal pricePerNight, User host, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.pricePerNight = pricePerNight;
        this.host = host;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static PropertyBuilder builder() {
        return new PropertyBuilder();
    }

    public static class PropertyBuilder {
        private Long id;
        private String title;
        private String description;
        private String location;
        private BigDecimal pricePerNight;
        private User host;
        private LocalDateTime createdAt;

        public PropertyBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PropertyBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PropertyBuilder description(String description) {
            this.description = description;
            return this;
        }

        public PropertyBuilder location(String location) {
            this.location = location;
            return this;
        }

        public PropertyBuilder pricePerNight(BigDecimal pricePerNight) {
            this.pricePerNight = pricePerNight;
            return this;
        }

        public PropertyBuilder host(User host) {
            this.host = host;
            return this;
        }

        public PropertyBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Property build() {
            return new Property(id, title, description, location, pricePerNight, host, createdAt);
        }
    }
}

package com.staynest.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PropertyResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private BigDecimal pricePerNight;
    private Long hostId;
    private String hostName;
    private Double averageRating;
    private LocalDateTime createdAt;

    public PropertyResponse() {}

    public PropertyResponse(Long id, String title, String description, String location, BigDecimal pricePerNight, Long hostId, String hostName, Double averageRating, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.pricePerNight = pricePerNight;
        this.hostId = hostId;
        this.hostName = hostName;
        this.averageRating = averageRating;
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

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static PropertyResponseBuilder builder() {
        return new PropertyResponseBuilder();
    }

    public static class PropertyResponseBuilder {
        private Long id;
        private String title;
        private String description;
        private String location;
        private BigDecimal pricePerNight;
        private Long hostId;
        private String hostName;
        private Double averageRating;
        private LocalDateTime createdAt;

        public PropertyResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PropertyResponseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PropertyResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public PropertyResponseBuilder location(String location) {
            this.location = location;
            return this;
        }

        public PropertyResponseBuilder pricePerNight(BigDecimal pricePerNight) {
            this.pricePerNight = pricePerNight;
            return this;
        }

        public PropertyResponseBuilder hostId(Long hostId) {
            this.hostId = hostId;
            return this;
        }

        public PropertyResponseBuilder hostName(String hostName) {
            this.hostName = hostName;
            return this;
        }

        public PropertyResponseBuilder averageRating(Double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public PropertyResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PropertyResponse build() {
            return new PropertyResponse(id, title, description, location, pricePerNight, hostId, hostName, averageRating, createdAt);
        }
    }
}

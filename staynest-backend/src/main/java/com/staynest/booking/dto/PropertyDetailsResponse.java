package com.staynest.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PropertyDetailsResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private BigDecimal pricePerNight;
    private UserResponse host;
    private List<AvailabilityResponse> availabilities;
    private List<ReviewResponse> reviews;
    private Double averageRating;
    private LocalDateTime createdAt;

    public PropertyDetailsResponse() {}

    public PropertyDetailsResponse(Long id, String title, String description, String location, BigDecimal pricePerNight, UserResponse host, List<AvailabilityResponse> availabilities, List<ReviewResponse> reviews, Double averageRating, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.pricePerNight = pricePerNight;
        this.host = host;
        this.availabilities = availabilities;
        this.reviews = reviews;
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

    public UserResponse getHost() {
        return host;
    }

    public void setHost(UserResponse host) {
        this.host = host;
    }

    public List<AvailabilityResponse> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<AvailabilityResponse> availabilities) {
        this.availabilities = availabilities;
    }

    public List<ReviewResponse> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewResponse> reviews) {
        this.reviews = reviews;
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

    public static PropertyDetailsResponseBuilder builder() {
        return new PropertyDetailsResponseBuilder();
    }

    public static class PropertyDetailsResponseBuilder {
        private Long id;
        private String title;
        private String description;
        private String location;
        private BigDecimal pricePerNight;
        private UserResponse host;
        private List<AvailabilityResponse> availabilities;
        private List<ReviewResponse> reviews;
        private Double averageRating;
        private LocalDateTime createdAt;

        public PropertyDetailsResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PropertyDetailsResponseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PropertyDetailsResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public PropertyDetailsResponseBuilder location(String location) {
            this.location = location;
            return this;
        }

        public PropertyDetailsResponseBuilder pricePerNight(BigDecimal pricePerNight) {
            this.pricePerNight = pricePerNight;
            return this;
        }

        public PropertyDetailsResponseBuilder host(UserResponse host) {
            this.host = host;
            return this;
        }

        public PropertyDetailsResponseBuilder availabilities(List<AvailabilityResponse> availabilities) {
            this.availabilities = availabilities;
            return this;
        }

        public PropertyDetailsResponseBuilder reviews(List<ReviewResponse> reviews) {
            this.reviews = reviews;
            return this;
        }

        public PropertyDetailsResponseBuilder averageRating(Double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public PropertyDetailsResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PropertyDetailsResponse build() {
            return new PropertyDetailsResponse(id, title, description, location, pricePerNight, host, availabilities, reviews, averageRating, createdAt);
        }
    }
}

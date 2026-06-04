package com.staynest.booking.dto;

import java.time.LocalDateTime;

public class ReviewResponse {
    private Long id;
    private Long propertyId;
    private Long guestId;
    private String guestName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewResponse() {}

    public ReviewResponse(Long id, Long propertyId, Long guestId, String guestName, Integer rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.propertyId = propertyId;
        this.guestId = guestId;
        this.guestName = guestName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
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

    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static ReviewResponseBuilder builder() {
        return new ReviewResponseBuilder();
    }

    public static class ReviewResponseBuilder {
        private Long id;
        private Long propertyId;
        private Long guestId;
        private String guestName;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;

        public ReviewResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ReviewResponseBuilder propertyId(Long propertyId) {
            this.propertyId = propertyId;
            return this;
        }

        public ReviewResponseBuilder guestId(Long guestId) {
            this.guestId = guestId;
            return this;
        }

        public ReviewResponseBuilder guestName(String guestName) {
            this.guestName = guestName;
            return this;
        }

        public ReviewResponseBuilder rating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public ReviewResponseBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public ReviewResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ReviewResponse build() {
            return new ReviewResponse(id, propertyId, guestId, guestName, rating, comment, createdAt);
        }
    }
}

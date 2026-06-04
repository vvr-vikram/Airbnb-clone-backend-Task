package com.staynest.booking.dto;

import java.math.BigDecimal;

public class BookingStatsResponse {
    private Long propertyId;
    private String propertyTitle;
    private Long totalBookings;
    private BigDecimal totalEarnings;
    private Long activeBookingsCount;
    private Long completedBookingsCount;
    private Long cancelledBookingsCount;
    private Double averageRating;

    public BookingStatsResponse() {}

    public BookingStatsResponse(Long propertyId, String propertyTitle, Long totalBookings, BigDecimal totalEarnings, Long activeBookingsCount, Long completedBookingsCount, Long cancelledBookingsCount, Double averageRating) {
        this.propertyId = propertyId;
        this.propertyTitle = propertyTitle;
        this.totalBookings = totalBookings;
        this.totalEarnings = totalEarnings;
        this.activeBookingsCount = activeBookingsCount;
        this.completedBookingsCount = completedBookingsCount;
        this.cancelledBookingsCount = cancelledBookingsCount;
        this.averageRating = averageRating;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }

    public Long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public BigDecimal getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(BigDecimal totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public Long getActiveBookingsCount() {
        return activeBookingsCount;
    }

    public void setActiveBookingsCount(Long activeBookingsCount) {
        this.activeBookingsCount = activeBookingsCount;
    }

    public Long getCompletedBookingsCount() {
        return completedBookingsCount;
    }

    public void setCompletedBookingsCount(Long completedBookingsCount) {
        this.completedBookingsCount = completedBookingsCount;
    }

    public Long getCancelledBookingsCount() {
        return cancelledBookingsCount;
    }

    public void setCancelledBookingsCount(Long cancelledBookingsCount) {
        this.cancelledBookingsCount = cancelledBookingsCount;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public static BookingStatsResponseBuilder builder() {
        return new BookingStatsResponseBuilder();
    }

    public static class BookingStatsResponseBuilder {
        private Long propertyId;
        private String propertyTitle;
        private Long totalBookings;
        private BigDecimal totalEarnings;
        private Long activeBookingsCount;
        private Long completedBookingsCount;
        private Long cancelledBookingsCount;
        private Double averageRating;

        public BookingStatsResponseBuilder propertyId(Long propertyId) {
            this.propertyId = propertyId;
            return this;
        }

        public BookingStatsResponseBuilder propertyTitle(String propertyTitle) {
            this.propertyTitle = propertyTitle;
            return this;
        }

        public BookingStatsResponseBuilder totalBookings(Long totalBookings) {
            this.totalBookings = totalBookings;
            return this;
        }

        public BookingStatsResponseBuilder totalEarnings(BigDecimal totalEarnings) {
            this.totalEarnings = totalEarnings;
            return this;
        }

        public BookingStatsResponseBuilder activeBookingsCount(Long activeBookingsCount) {
            this.activeBookingsCount = activeBookingsCount;
            return this;
        }

        public BookingStatsResponseBuilder completedBookingsCount(Long completedBookingsCount) {
            this.completedBookingsCount = completedBookingsCount;
            return this;
        }

        public BookingStatsResponseBuilder cancelledBookingsCount(Long cancelledBookingsCount) {
            this.cancelledBookingsCount = cancelledBookingsCount;
            return this;
        }

        public BookingStatsResponseBuilder averageRating(Double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public BookingStatsResponse build() {
            return new BookingStatsResponse(propertyId, propertyTitle, totalBookings, totalEarnings, activeBookingsCount, completedBookingsCount, cancelledBookingsCount, averageRating);
        }
    }
}

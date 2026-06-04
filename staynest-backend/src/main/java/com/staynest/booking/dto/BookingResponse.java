package com.staynest.booking.dto;

import com.staynest.booking.entity.Booking.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponse {
    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private Long guestId;
    private String guestName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private LocalDateTime createdAt;

    public BookingResponse() {}

    public BookingResponse(Long id, Long propertyId, String propertyTitle, Long guestId, String guestName, LocalDate startDate, LocalDate endDate, BigDecimal totalPrice, BookingStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.propertyId = propertyId;
        this.propertyTitle = propertyTitle;
        this.guestId = guestId;
        this.guestName = guestName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
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

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static BookingResponseBuilder builder() {
        return new BookingResponseBuilder();
    }

    public static class BookingResponseBuilder {
        private Long id;
        private Long propertyId;
        private String propertyTitle;
        private Long guestId;
        private String guestName;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal totalPrice;
        private BookingStatus status;
        private LocalDateTime createdAt;

        public BookingResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BookingResponseBuilder propertyId(Long propertyId) {
            this.propertyId = propertyId;
            return this;
        }

        public BookingResponseBuilder propertyTitle(String propertyTitle) {
            this.propertyTitle = propertyTitle;
            return this;
        }

        public BookingResponseBuilder guestId(Long guestId) {
            this.guestId = guestId;
            return this;
        }

        public BookingResponseBuilder guestName(String guestName) {
            this.guestName = guestName;
            return this;
        }

        public BookingResponseBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public BookingResponseBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public BookingResponseBuilder totalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public BookingResponseBuilder status(BookingStatus status) {
            this.status = status;
            return this;
        }

        public BookingResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BookingResponse build() {
            return new BookingResponse(id, propertyId, propertyTitle, guestId, guestName, startDate, endDate, totalPrice, status, createdAt);
        }
    }
}

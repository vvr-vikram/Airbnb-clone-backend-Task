package com.staynest.booking.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_bookings_dates", columnList = "property_id, start_date, end_date, status"),
    @Index(name = "idx_bookings_guest", columnList = "guest_id")
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum BookingStatus {
        REQUESTED,
        CONFIRMED,
        CANCELLED,
        COMPLETED
    }

    public Booking() {}

    public Booking(Long id, Property property, User guest, LocalDate startDate, LocalDate endDate, BigDecimal totalPrice, BookingStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.property = property;
        this.guest = guest;
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

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
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

    public static BookingBuilder builder() {
        return new BookingBuilder();
    }

    public static class BookingBuilder {
        private Long id;
        private Property property;
        private User guest;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal totalPrice;
        private BookingStatus status;
        private LocalDateTime createdAt;

        public BookingBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BookingBuilder property(Property property) {
            this.property = property;
            return this;
        }

        public BookingBuilder guest(User guest) {
            this.guest = guest;
            return this;
        }

        public BookingBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public BookingBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public BookingBuilder totalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public BookingBuilder status(BookingStatus status) {
            this.status = status;
            return this;
        }

        public BookingBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Booking build() {
            return new Booking(id, property, guest, startDate, endDate, totalPrice, status, createdAt);
        }
    }
}

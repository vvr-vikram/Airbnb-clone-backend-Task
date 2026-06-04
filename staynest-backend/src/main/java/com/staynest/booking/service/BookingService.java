package com.staynest.booking.service;

import com.staynest.booking.dto.BookingRequest;
import com.staynest.booking.dto.BookingResponse;
import com.staynest.booking.entity.Booking;
import com.staynest.booking.entity.Booking.BookingStatus;
import com.staynest.booking.entity.Property;
import com.staynest.booking.entity.PropertyAvailability;
import com.staynest.booking.entity.User;
import com.staynest.booking.entity.User.Role;
import com.staynest.booking.exception.BadRequestException;
import com.staynest.booking.exception.BookingConflictException;
import com.staynest.booking.exception.ResourceNotFoundException;
import com.staynest.booking.exception.UnauthorizedException;
import com.staynest.booking.repository.BookingRepository;
import com.staynest.booking.repository.PropertyAvailabilityRepository;
import com.staynest.booking.repository.PropertyRepository;
import com.staynest.booking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyAvailabilityRepository availabilityRepository;

    public BookingService(BookingRepository bookingRepository, PropertyRepository propertyRepository,
                          UserRepository userRepository, PropertyAvailabilityRepository availabilityRepository) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for Property ID: {} by Guest ID: {}", request.getPropertyId(), request.getGuestId());

        User guest = userRepository.findById(request.getGuestId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with ID: " + request.getGuestId()));

        if (guest.getRole() != Role.GUEST) {
            throw new BadRequestException("Only guests can make bookings");
        }

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        
        if (startDate.isBefore(LocalDate.now())) {
            throw new BadRequestException("Booking start date cannot be in the past");
        }
        if (!startDate.isBefore(endDate)) {
            throw new BadRequestException("Booking start date must be before end date");
        }

        long nights = ChronoUnit.DAYS.between(startDate, endDate);
        if (nights <= 0) {
            throw new BadRequestException("Booking must be for at least 1 night");
        }

        Property property = propertyRepository.findByIdWithWriteLock(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + request.getPropertyId()));

        if (property.getHost().getId().equals(guest.getId())) {
            throw new BadRequestException("Hosts cannot book their own properties");
        }

        List<PropertyAvailability> coveringAvailabilities = availabilityRepository.findCoveringAvailability(
                property.getId(), startDate, endDate
        );
        if (coveringAvailabilities.isEmpty()) {
            log.warn("Booking failed: Property {} has no availability range covering {} to {}", property.getId(), startDate, endDate);
            throw new BadRequestException("Property is not available for the requested dates");
        }

        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                property.getId(), startDate, endDate, List.of(BookingStatus.CONFIRMED, BookingStatus.REQUESTED)
        );
        if (!overlappingBookings.isEmpty()) {
            log.warn("Booking failed: Property {} has overlapping active bookings in range {} to {}", property.getId(), startDate, endDate);
            throw new BookingConflictException("Property is already booked for the selected dates");
        }

        BigDecimal totalPrice = property.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = Booking.builder()
                .property(property)
                .guest(guest)
                .startDate(startDate)
                .endDate(endDate)
                .totalPrice(totalPrice)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created and CONFIRMED with ID: {}, Total Price: INR {}", savedBooking.getId(), totalPrice);

        return convertToResponse(savedBooking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, Long userId) {
        log.info("Attempting to cancel booking ID: {} by User ID: {}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        boolean isGuest = booking.getGuest().getId().equals(userId);
        boolean isHost = booking.getProperty().getHost().getId().equals(userId);

        if (!isGuest && !isHost) {
            throw new UnauthorizedException("You are not authorized to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking ID: {} has been CANCELLED", updatedBooking.getId());

        return convertToResponse(updatedBooking);
    }

    @Transactional
    public BookingResponse completeBooking(Long bookingId) {
        log.info("Completing booking ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Only confirmed bookings can be completed");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking ID: {} has been COMPLETED", updatedBooking.getId());

        return convertToResponse(updatedBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(Long userId) {
        log.info("Fetching booking history for User ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<Booking> bookings;
        if (user.getRole() == Role.GUEST) {
            bookings = bookingRepository.findByGuestIdOrderByCreatedAtDesc(userId);
        } else {
            bookings = bookingRepository.findByHostIdOrderByCreatedAtDesc(userId);
        }

        return bookings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse convertToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .propertyId(booking.getProperty().getId())
                .propertyTitle(booking.getProperty().getTitle())
                .guestId(booking.getGuest().getId())
                .guestName(booking.getGuest().getName())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}

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
import com.staynest.booking.exception.UnauthorizedException;
import com.staynest.booking.repository.BookingRepository;
import com.staynest.booking.repository.PropertyAvailabilityRepository;
import com.staynest.booking.repository.PropertyRepository;
import com.staynest.booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PropertyAvailabilityRepository availabilityRepository;

    @InjectMocks
    private BookingService bookingService;

    private User guest;
    private User host;
    private Property property;
    private PropertyAvailability availability;

    @BeforeEach
    void setUp() {
        guest = User.builder()
                .id(1L)
                .name("Ananya Iyer")
                .email("ananya@staynest.in")
                .role(Role.GUEST)
                .createdAt(LocalDateTime.now())
                .build();

        host = User.builder()
                .id(2L)
                .name("Aarav Sharma")
                .email("aarav@staynest.in")
                .role(Role.HOST)
                .createdAt(LocalDateTime.now())
                .build();

        property = Property.builder()
                .id(1L)
                .title("Taj Luxury Villa")
                .description("Luxury beachfront villa in Goa")
                .location("Goa")
                .pricePerNight(BigDecimal.valueOf(10000.00))
                .host(host)
                .createdAt(LocalDateTime.now())
                .build();

        availability = PropertyAvailability.builder()
                .id(1L)
                .property(property)
                .availableFrom(LocalDate.now().plusDays(1))
                .availableTo(LocalDate.now().plusDays(10))
                .build();
    }

    @Test
    void createBooking_Success() {
        BookingRequest request = new BookingRequest();
        request.setGuestId(1L);
        request.setPropertyId(1L);
        request.setStartDate(LocalDate.now().plusDays(2));
        request.setEndDate(LocalDate.now().plusDays(5)); // 3 nights

        when(userRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(propertyRepository.findByIdWithWriteLock(1L)).thenReturn(Optional.of(property));
        when(availabilityRepository.findCoveringAvailability(eq(1L), any(), any()))
                .thenReturn(Collections.singletonList(availability));
        when(bookingRepository.findOverlappingBookings(eq(1L), any(), any(), any()))
                .thenReturn(new ArrayList<>());

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(10L);
            b.setCreatedAt(LocalDateTime.now());
            return b;
        });

        BookingResponse response = bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getPropertyId());
        assertEquals("Taj Luxury Villa", response.getPropertyTitle());
        assertEquals(1L, response.getGuestId());
        assertEquals("Ananya Iyer", response.getGuestName());
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        assertEquals(BigDecimal.valueOf(30000.00), response.getTotalPrice()); // 3 nights * 10000

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_Fail_UserIsHost() {
        BookingRequest request = new BookingRequest();
        request.setGuestId(2L); // host id
        request.setPropertyId(1L);
        request.setStartDate(LocalDate.now().plusDays(2));
        request.setEndDate(LocalDate.now().plusDays(5));

        when(userRepository.findById(2L)).thenReturn(Optional.of(host)); // role is HOST

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(request));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_Fail_DatesInPast() {
        BookingRequest request = new BookingRequest();
        request.setGuestId(1L);
        request.setPropertyId(1L);
        request.setStartDate(LocalDate.now().minusDays(2)); // past date
        request.setEndDate(LocalDate.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(guest));

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(request));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_Fail_NoAvailabilityCovering() {
        BookingRequest request = new BookingRequest();
        request.setGuestId(1L);
        request.setPropertyId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(15)); // Exceeds availability end (plusDays(10))

        when(userRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(propertyRepository.findByIdWithWriteLock(1L)).thenReturn(Optional.of(property));
        when(availabilityRepository.findCoveringAvailability(eq(1L), any(), any()))
                .thenReturn(new ArrayList<>()); // empty, not covered

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(request));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_Fail_OverlappingBooking() {
        BookingRequest request = new BookingRequest();
        request.setGuestId(1L);
        request.setPropertyId(1L);
        request.setStartDate(LocalDate.now().plusDays(2));
        request.setEndDate(LocalDate.now().plusDays(5));

        when(userRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(propertyRepository.findByIdWithWriteLock(1L)).thenReturn(Optional.of(property));
        when(availabilityRepository.findCoveringAvailability(eq(1L), any(), any()))
                .thenReturn(Collections.singletonList(availability));

        Booking existingBooking = Booking.builder()
                .id(5L)
                .property(property)
                .guest(guest)
                .startDate(LocalDate.now().plusDays(4))
                .endDate(LocalDate.now().plusDays(6)) // Overlaps
                .status(BookingStatus.CONFIRMED)
                .build();

        when(bookingRepository.findOverlappingBookings(eq(1L), any(), any(), any()))
                .thenReturn(Collections.singletonList(existingBooking));

        assertThrows(BookingConflictException.class, () -> bookingService.createBooking(request));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancelBooking_Success_ByGuest() {
        Booking booking = Booking.builder()
                .id(10L)
                .property(property)
                .guest(guest)
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(5))
                .totalPrice(BigDecimal.valueOf(30000.00))
                .status(BookingStatus.CONFIRMED)
                .build();

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse response = bookingService.cancelBooking(10L, 1L); // cancelled by guest id 1

        assertNotNull(response);
        assertEquals(BookingStatus.CANCELLED, response.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void cancelBooking_Fail_Unauthorized() {
        Booking booking = Booking.builder()
                .id(10L)
                .property(property)
                .guest(guest)
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(5))
                .status(BookingStatus.CONFIRMED)
                .build();

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        assertThrows(UnauthorizedException.class, () -> bookingService.cancelBooking(10L, 99L)); // user id 99 is random
        verify(bookingRepository, never()).save(any());
    }
}

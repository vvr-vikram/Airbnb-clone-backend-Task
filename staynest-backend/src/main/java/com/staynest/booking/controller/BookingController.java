package com.staynest.booking.controller;

import com.staynest.booking.dto.BookingRequest;
import com.staynest.booking.dto.BookingResponse;
import com.staynest.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@Tag(name = "Booking Management", description = "APIs for creating, cancelling, completing, and tracking property bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Create booking request", description = "Submit a booking request. Verifies availability and overlapping dates, and instantly sets status to CONFIRMED if verification succeeds.")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel booking", description = "Cancel an active booking (Requires userId of either the booking guest or the property host)")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id,
            @RequestParam Long userId) {
        BookingResponse response = bookingService.cancelBooking(id, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Complete booking", description = "Complete a stay (called when checkout date finishes; status transitions to COMPLETED)")
    public ResponseEntity<BookingResponse> completeBooking(@PathVariable Long id) {
        BookingResponse response = bookingService.completeBooking(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Fetch user booking history", description = "Retrieve booking history. Guests see stays they booked; Hosts see stays booked at their properties.")
    public ResponseEntity<List<BookingResponse>> getUserBookings(@PathVariable Long userId) {
        List<BookingResponse> responses = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(responses);
    }
}

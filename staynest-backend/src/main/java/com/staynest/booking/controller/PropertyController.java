package com.staynest.booking.controller;

import com.staynest.booking.dto.*;
import com.staynest.booking.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/properties")
@Tag(name = "Property Management", description = "APIs for creating, updating, searching properties, and managing availabilities")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    @Operation(summary = "Create property listing", description = "Add a new property to the platform (Host role required)")
    public ResponseEntity<PropertyResponse> createProperty(@Valid @RequestBody PropertyRequest request) {
        PropertyResponse response = propertyService.createProperty(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update property listing", description = "Modify details of an existing property listing")
    public ResponseEntity<PropertyResponse> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyRequest request) {
        PropertyResponse response = propertyService.updateProperty(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/availabilities")
    @Operation(summary = "Set property availability", description = "Define a date range during which the property can be booked by guests")
    public ResponseEntity<AvailabilityResponse> addAvailability(
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityRequest request) {
        AvailabilityResponse response = propertyService.addAvailability(id, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Search properties", description = "Retrieve a paginated list of properties matching optional filters like location, price range, and minimum rating")
    public ResponseEntity<PaginatedResponse<PropertyResponse>> searchProperties(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<PropertyResponse> response = propertyService.searchProperties(location, minPrice, maxPrice, minRating, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get property details by ID", description = "Fetch a detailed profile of the property, including host details, active availabilities, and guest reviews")
    public ResponseEntity<PropertyDetailsResponse> getPropertyDetails(@PathVariable Long id) {
        PropertyDetailsResponse response = propertyService.getPropertyDetails(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    @Operation(summary = "Get popular properties", description = "Retrieve properties ordered by average rating and booking counts")
    public ResponseEntity<List<PropertyResponse>> getPopularProperties(
            @RequestParam(defaultValue = "5") int limit) {
        List<PropertyResponse> responses = propertyService.getPopularProperties(limit);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/bookings")
    @Operation(summary = "View bookings for a property", description = "Retrieve all bookings associated with this property (Host authorization verified using hostId)")
    public ResponseEntity<List<BookingResponse>> getPropertyBookings(
            @PathVariable Long id,
            @RequestParam Long hostId) {
        List<BookingResponse> responses = propertyService.getPropertyBookings(id, hostId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Get property booking statistics", description = "Retrieve revenue, booking metrics, and rating analytics for a property (Host authorization verified using hostId)")
    public ResponseEntity<BookingStatsResponse> getPropertyStats(
            @PathVariable Long id,
            @RequestParam Long hostId) {
        BookingStatsResponse response = propertyService.getPropertyStats(id, hostId);
        return ResponseEntity.ok(response);
    }
}

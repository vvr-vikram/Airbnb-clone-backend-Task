package com.staynest.booking.service;

import com.staynest.booking.dto.*;
import com.staynest.booking.entity.*;
import com.staynest.booking.entity.Booking.BookingStatus;
import com.staynest.booking.entity.User.Role;
import com.staynest.booking.exception.BadRequestException;
import com.staynest.booking.exception.ResourceNotFoundException;
import com.staynest.booking.exception.UnauthorizedException;
import com.staynest.booking.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private static final Logger log = LoggerFactory.getLogger(PropertyService.class);

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyAvailabilityRepository availabilityRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository,
                           PropertyAvailabilityRepository availabilityRepository, BookingRepository bookingRepository,
                           ReviewRepository reviewRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.availabilityRepository = availabilityRepository;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public PropertyResponse createProperty(PropertyRequest request) {
        log.info("Creating property listing: {} by Host: {}", request.getTitle(), request.getHostId());
        
        User host = userRepository.findById(request.getHostId())
                .orElseThrow(() -> new ResourceNotFoundException("Host not found with ID: " + request.getHostId()));

        if (host.getRole() != Role.HOST) {
            log.warn("Failed to create property: User {} is not a Host", host.getId());
            throw new BadRequestException("Only hosts can create property listings");
        }

        Property property = Property.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .pricePerNight(request.getPricePerNight())
                .host(host)
                .build();

        Property savedProperty = propertyRepository.save(property);
        log.info("Property created successfully with ID: {}", savedProperty.getId());

        return convertToResponse(savedProperty, 0.0);
    }

    @Transactional
    public PropertyResponse updateProperty(Long id, PropertyRequest request) {
        log.info("Updating property ID: {} by Host: {}", id, request.getHostId());

        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + id));

        if (!property.getHost().getId().equals(request.getHostId())) {
            log.warn("Unauthorized update attempt: User {} is not the host of property {}", request.getHostId(), id);
            throw new UnauthorizedException("You are not authorized to update this property");
        }

        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setLocation(request.getLocation());
        property.setPricePerNight(request.getPricePerNight());
        
        Property updatedProperty = propertyRepository.save(property);
        log.info("Property ID: {} updated successfully", updatedProperty.getId());

        double avgRating = calculateAverageRating(id);
        return convertToResponse(updatedProperty, avgRating);
    }

    @Transactional
    public AvailabilityResponse addAvailability(Long propertyId, AvailabilityRequest request) {
        log.info("Adding availability for property ID: {} from {} to {}", propertyId, request.getAvailableFrom(), request.getAvailableTo());

        if (request.getAvailableFrom().isAfter(request.getAvailableTo())) {
            throw new BadRequestException("Available-from date must be before or equal to available-to date");
        }

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));

        PropertyAvailability availability = PropertyAvailability.builder()
                .property(property)
                .availableFrom(request.getAvailableFrom())
                .availableTo(request.getAvailableTo())
                .build();

        PropertyAvailability savedAvailability = availabilityRepository.save(availability);
        log.info("Availability ID: {} added successfully", savedAvailability.getId());

        return convertToAvailabilityResponse(savedAvailability);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<PropertyResponse> searchProperties(String location, BigDecimal minPrice, BigDecimal maxPrice, Double minRating, int page, int size) {
        log.info("Searching properties with parameters - Location: {}, Price: [{} - {}], Min Rating: {}, Page: {}, Size: {}", 
                location, minPrice, maxPrice, minRating, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Property> propertyPage = propertyRepository.searchProperties(location, minPrice, maxPrice, minRating, pageable);

        List<PropertyResponse> content = propertyPage.getContent().stream()
                .map(prop -> convertToResponse(prop, calculateAverageRating(prop.getId())))
                .collect(Collectors.toList());

        return PaginatedResponse.<PropertyResponse>builder()
                .content(content)
                .pageNumber(propertyPage.getNumber())
                .pageSize(propertyPage.getSize())
                .totalElements(propertyPage.getTotalElements())
                .totalPages(propertyPage.getTotalPages())
                .last(propertyPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PropertyDetailsResponse getPropertyDetails(Long id) {
        log.info("Fetching details for property ID: {}", id);

        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + id));

        List<PropertyAvailability> availabilities = availabilityRepository.findByPropertyId(id);
        List<Review> reviews = reviewRepository.findByPropertyIdOrderByCreatedAtDesc(id);

        double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        UserResponse hostResponse = UserResponse.builder()
                .id(property.getHost().getId())
                .name(property.getHost().getName())
                .email(property.getHost().getEmail())
                .role(property.getHost().getRole())
                .createdAt(property.getHost().getCreatedAt())
                .build();

        List<AvailabilityResponse> availabilityResponses = availabilities.stream()
                .map(this::convertToAvailabilityResponse)
                .collect(Collectors.toList());

        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(rev -> ReviewResponse.builder()
                        .id(rev.getId())
                        .propertyId(id)
                        .guestId(rev.getGuest().getId())
                        .guestName(rev.getGuest().getName())
                        .rating(rev.getRating())
                        .comment(rev.getComment())
                        .createdAt(rev.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return PropertyDetailsResponse.builder()
                .id(property.getId())
                .title(property.getTitle())
                .description(property.getDescription())
                .location(property.getLocation())
                .pricePerNight(property.getPricePerNight())
                .host(hostResponse)
                .availabilities(availabilityResponses)
                .reviews(reviewResponses)
                .averageRating(avgRating)
                .createdAt(property.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<PropertyResponse> getPopularProperties(int limit) {
        log.info("Fetching popular properties with limit: {}", limit);
        Pageable pageable = PageRequest.of(0, limit);
        List<Property> popular = propertyRepository.findPopularProperties(pageable);

        return popular.stream()
                .map(prop -> convertToResponse(prop, calculateAverageRating(prop.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getPropertyBookings(Long propertyId, Long hostId) {
        log.info("Fetching bookings for property ID: {} for Host ID: {}", propertyId, hostId);

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));

        if (!property.getHost().getId().equals(hostId)) {
            throw new UnauthorizedException("Only the host can view property bookings");
        }

        List<Booking> bookings = bookingRepository.findByPropertyIdOrderByStartDateDesc(propertyId);
        
        return bookings.stream()
                .map(b -> BookingResponse.builder()
                        .id(b.getId())
                        .propertyId(b.getProperty().getId())
                        .propertyTitle(b.getProperty().getTitle())
                        .guestId(b.getGuest().getId())
                        .guestName(b.getGuest().getName())
                        .startDate(b.getStartDate())
                        .endDate(b.getEndDate())
                        .totalPrice(b.getTotalPrice())
                        .status(b.getStatus())
                        .createdAt(b.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingStatsResponse getPropertyStats(Long propertyId, Long hostId) {
        log.info("Calculating statistics for property ID: {} for Host ID: {}", propertyId, hostId);

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));

        if (!property.getHost().getId().equals(hostId)) {
            throw new UnauthorizedException("Only the host can view property statistics");
        }

        List<Booking> bookings = bookingRepository.findByPropertyIdOrderByStartDateDesc(propertyId);
        List<Review> reviews = reviewRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId);

        long totalBookings = bookings.size();
        BigDecimal totalEarnings = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.COMPLETED)
                .map(Booking::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long activeCount = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.REQUESTED)
                .count();

        long completedCount = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                .count();

        long cancelledCount = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                .count();

        double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return BookingStatsResponse.builder()
                .propertyId(propertyId)
                .propertyTitle(property.getTitle())
                .totalBookings(totalBookings)
                .totalEarnings(totalEarnings)
                .activeBookingsCount(activeCount)
                .completedBookingsCount(completedCount)
                .cancelledBookingsCount(cancelledCount)
                .averageRating(avgRating)
                .build();
    }

    private double calculateAverageRating(Long propertyId) {
        List<Review> reviews = reviewRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId);
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    private PropertyResponse convertToResponse(Property property, double avgRating) {
        return PropertyResponse.builder()
                .id(property.getId())
                .title(property.getTitle())
                .description(property.getDescription())
                .location(property.getLocation())
                .pricePerNight(property.getPricePerNight())
                .hostId(property.getHost().getId())
                .hostName(property.getHost().getName())
                .averageRating(avgRating)
                .createdAt(property.getCreatedAt())
                .build();
    }

    private AvailabilityResponse convertToAvailabilityResponse(PropertyAvailability availability) {
        return AvailabilityResponse.builder()
                .id(availability.getId())
                .propertyId(availability.getProperty().getId())
                .availableFrom(availability.getAvailableFrom())
                .availableTo(availability.getAvailableTo())
                .build();
    }
}

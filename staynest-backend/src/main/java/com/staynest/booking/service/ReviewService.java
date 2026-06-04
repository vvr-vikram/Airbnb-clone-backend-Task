package com.staynest.booking.service;

import com.staynest.booking.dto.ReviewRequest;
import com.staynest.booking.dto.ReviewResponse;
import com.staynest.booking.entity.Booking.BookingStatus;
import com.staynest.booking.entity.Property;
import com.staynest.booking.entity.Review;
import com.staynest.booking.entity.User;
import com.staynest.booking.entity.User.Role;
import com.staynest.booking.exception.BadRequestException;
import com.staynest.booking.exception.ResourceNotFoundException;
import com.staynest.booking.repository.BookingRepository;
import com.staynest.booking.repository.PropertyRepository;
import com.staynest.booking.repository.ReviewRepository;
import com.staynest.booking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public ReviewService(ReviewRepository reviewRepository, PropertyRepository propertyRepository,
                         UserRepository userRepository, BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public ReviewResponse addReview(ReviewRequest request) {
        log.info("Adding review for Property ID: {} by Guest ID: {}", request.getPropertyId(), request.getGuestId());

        User guest = userRepository.findById(request.getGuestId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with ID: " + request.getGuestId()));

        if (guest.getRole() != Role.GUEST) {
            throw new BadRequestException("Only guests can submit reviews");
        }

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + request.getPropertyId()));

        boolean hasCompletedStay = bookingRepository.existsByPropertyIdAndGuestIdAndStatus(
                property.getId(), guest.getId(), BookingStatus.COMPLETED
        );
        if (!hasCompletedStay) {
            log.warn("Review submission rejected: Guest {} has no COMPLETED booking for property {}", guest.getId(), property.getId());
            throw new BadRequestException("You can only review properties where you have a completed stay");
        }

        boolean alreadyReviewed = reviewRepository.existsByPropertyIdAndGuestId(property.getId(), guest.getId());
        if (alreadyReviewed) {
            throw new BadRequestException("You have already reviewed this property");
        }

        Review review = Review.builder()
                .property(property)
                .guest(guest)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("Review added successfully with ID: {}", savedReview.getId());

        return convertToResponse(savedReview);
    }

    private ReviewResponse convertToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .propertyId(review.getProperty().getId())
                .guestId(review.getGuest().getId())
                .guestName(review.getGuest().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}

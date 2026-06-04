package com.staynest.booking.controller;

import com.staynest.booking.dto.ReviewRequest;
import com.staynest.booking.dto.ReviewResponse;
import com.staynest.booking.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews")
@Tag(name = "Review Management", description = "APIs for submitting ratings and reviews for properties after stay completion")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(summary = "Submit a property review", description = "Submit a rating and comment for a property (Requires a guest ID with a completed stay history at that property)")
    public ResponseEntity<ReviewResponse> addReview(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.addReview(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

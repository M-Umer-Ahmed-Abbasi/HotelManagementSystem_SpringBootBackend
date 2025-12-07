package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.dto.ReviewRequest;
import com.esd.hotelmanagement.dto.ReviewResponse;
import com.esd.hotelmanagement.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for review operations.
 * 
 * @author Talha
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Guest reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reservations/{reservationId}/review")
    @Operation(summary = "Submit a review for a reservation", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long reservationId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.createReview(reservationId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/hotels/{hotelId}/reviews")
    @Operation(summary = "Get all reviews for a hotel")
    public ResponseEntity<List<ReviewResponse>> getReviewsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reviewService.getReviewsByHotel(hotelId));
    }

    @GetMapping("/hotels/{hotelId}/rating")
    @Operation(summary = "Get average rating for a hotel")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reviewService.getAverageRatingForHotel(hotelId));
    }

    @GetMapping("/reviews/me")
    @Operation(summary = "Get current user's reviews", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ReviewResponse>> getMyReviews() {
        return ResponseEntity.ok(reviewService.getMyReviews());
    }
}

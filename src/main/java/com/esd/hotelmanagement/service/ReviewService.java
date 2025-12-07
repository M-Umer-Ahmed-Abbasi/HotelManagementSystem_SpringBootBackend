package com.esd.hotelmanagement.service;

import com.esd.hotelmanagement.dto.ReviewRequest;
import com.esd.hotelmanagement.dto.ReviewResponse;
import com.esd.hotelmanagement.entity.Reservation;
import com.esd.hotelmanagement.entity.ReservationStatus;
import com.esd.hotelmanagement.entity.Review;
import com.esd.hotelmanagement.entity.User;
import com.esd.hotelmanagement.exception.BadRequestException;
import com.esd.hotelmanagement.exception.ResourceNotFoundException;
import com.esd.hotelmanagement.exception.UnauthorizedException;
import com.esd.hotelmanagement.repository.ReservationRepository;
import com.esd.hotelmanagement.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for review operations.
 * 
 * @author Talha
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final UserService userService;

    @Transactional
    public ReviewResponse createReview(Long reservationId, ReviewRequest request) {
        User user = userService.getCurrentUser();

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", reservationId));

        // Verify ownership
        if (!reservation.getGuest().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only review your own reservations");
        }

        // Verify reservation is completed or checkout date has passed
        if (reservation.getStatus() != ReservationStatus.COMPLETED &&
                reservation.getCheckOutDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("You can only review after checkout date");
        }

        // Check if review already exists
        if (reviewRepository.existsByReservationId(reservationId)) {
            throw new BadRequestException("Review already exists for this reservation");
        }

        Review review = Review.builder()
                .user(user)
                .reservation(reservation)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);
        return mapToResponse(review);
    }

    public List<ReviewResponse> getReviewsByHotel(Long hotelId) {
        return reviewRepository.findByHotelId(hotelId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getMyReviews() {
        User user = userService.getCurrentUser();
        return reviewRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReviewResponse getReviewByReservation(Long reservationId) {
        Review review = reviewRepository.findByReservationId(reservationId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Review for reservation", "reservationId", reservationId));
        return mapToResponse(review);
    }

    public Double getAverageRatingForHotel(Long hotelId) {
        return reviewRepository.calculateAverageRating(hotelId);
    }

    private ReviewResponse mapToResponse(Review review) {
        Reservation reservation = review.getReservation();
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFirstName() + " " + review.getUser().getLastName())
                .reservationId(reservation.getId())
                .hotelId(reservation.getRoom().getRoomType().getHotel().getId())
                .hotelName(reservation.getRoom().getRoomType().getHotel().getName())
                .build();
    }
}

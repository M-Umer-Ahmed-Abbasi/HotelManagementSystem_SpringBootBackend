package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.BaseIntegrationTest;
import com.esd.hotelmanagement.dto.ReviewRequest;
import com.esd.hotelmanagement.entity.*;
import com.esd.hotelmanagement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Review endpoints.
 */
public class ReviewControllerTest extends BaseIntegrationTest {

        @Autowired
        private UserRepository userRepository;
        @Autowired
        private HotelRepository hotelRepository;
        @Autowired
        private RoomTypeRepository roomTypeRepository;
        @Autowired
        private RoomRepository roomRepository;
        @Autowired
        private ReservationRepository reservationRepository;
        @Autowired
        private ReviewRepository reviewRepository;

        private Hotel testHotel;
        private User guest;
        private User owner;
        private Reservation testReservation;

        @BeforeEach
        void setUp() {
                // Create owner
                owner = User.builder()
                                .email("review-owner@example.com")
                                .password("password123")
                                .firstName("Hotel")
                                .lastName("Owner")
                                .role(Role.OWNER)
                                .enabled(true)
                                .build();
                userRepository.save(owner);

                // Create guest
                guest = User.builder()
                                .email("review-guest@example.com")
                                .password("password123")
                                .firstName("Review")
                                .lastName("Guest")
                                .role(Role.GUEST)
                                .enabled(true)
                                .build();
                userRepository.save(guest);

                // Create hotel
                testHotel = Hotel.builder()
                                .name("Review Test Hotel")
                                .owner(owner)
                                .starRating(4)
                                .location(Location.builder()
                                                .city("Review City")
                                                .country("USA")
                                                .address("456 Review St")
                                                .state("RC")
                                                .zipCode("67890")
                                                .build())
                                .cancellationPolicy(CancellationPolicy.builder()
                                                .name("Standard")
                                                .build())
                                .build();
                hotelRepository.save(testHotel);

                // Create room type and room
                RoomType roomType = RoomType.builder()
                                .name("Standard")
                                .basePrice(new BigDecimal("100.00"))
                                .maxOccupancy(2)
                                .hotel(testHotel)
                                .build();
                roomTypeRepository.save(roomType);

                Room room = Room.builder()
                                .roomNumber("R101")
                                .floor(1)
                                .roomType(roomType)
                                .status(RoomStatus.AVAILABLE)
                                .build();
                roomRepository.save(room);

                // Create a completed reservation for review
                testReservation = Reservation.builder()
                                .guest(guest)
                                .room(room)
                                .checkInDate(LocalDate.now().minusDays(5))
                                .checkOutDate(LocalDate.now().minusDays(2))
                                .guestCount(2)
                                .status(ReservationStatus.COMPLETED)
                                .totalPrice(new BigDecimal("300.00"))
                                .build();
                reservationRepository.save(testReservation);
        }

        @Test
        @DisplayName("Should create review for completed reservation")
        @WithMockUser(username = "review-guest@example.com", roles = { "GUEST" })
        void shouldCreateReview() throws Exception {
                ReviewRequest request = ReviewRequest.builder()
                                .rating(5)
                                .comment("Excellent stay! Highly recommended.")
                                .build();

                mockMvc.perform(post("/api/reservations/{reservationId}/review", testReservation.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.rating", is(5)));
        }

        @Test
        @DisplayName("Should get reviews for hotel")
        void shouldGetReviewsForHotel() throws Exception {
                // Create a review first
                Review review = Review.builder()
                                .user(guest)
                                .reservation(testReservation)
                                .rating(5)
                                .comment("Amazing!")
                                .build();
                reviewRepository.save(review);

                mockMvc.perform(get("/api/hotels/{hotelId}/reviews", testHotel.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].rating", is(5)));
        }

        @Test
        @DisplayName("Should get average rating for hotel")
        void shouldGetAverageRating() throws Exception {
                // Create reviews
                Review review1 = Review.builder()
                                .user(guest)
                                .reservation(testReservation)
                                .rating(5)
                                .comment("Perfect!")
                                .build();
                reviewRepository.save(review1);

                mockMvc.perform(get("/api/hotels/{hotelId}/rating", testHotel.getId()))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should require authentication to create review")
        void shouldRequireAuthForReview() throws Exception {
                ReviewRequest request = ReviewRequest.builder()
                                .rating(5)
                                .comment("Test")
                                .build();

                mockMvc.perform(post("/api/reservations/{reservationId}/review", testReservation.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden());
        }
}

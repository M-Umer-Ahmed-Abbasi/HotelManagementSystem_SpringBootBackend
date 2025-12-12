package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.BaseIntegrationTest;
import com.esd.hotelmanagement.dto.ReservationRequest;
import com.esd.hotelmanagement.entity.*;
import com.esd.hotelmanagement.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReservationFlowTest extends BaseIntegrationTest {

        @Autowired
        private UserRepository userRepository;
        @Autowired
        private HotelRepository hotelRepository;
        @Autowired
        private RoomTypeRepository roomTypeRepository;
        @Autowired
        private RoomRepository roomRepository;

        @Test
        @DisplayName("Should create reservation successfully")
        @WithMockUser(username = "guest@example.com", roles = { "GUEST" })
        void shouldCreateReservation() throws Exception {
                // 1. Setup Data
                // Owner
                User owner = User.builder()
                                .email("owner@example.com")
                                .password("password123")
                                .firstName("Owner")
                                .lastName("Hotel")
                                .role(Role.OWNER)
                                .build();
                userRepository.save(owner);

                // Guest (Current User)
                User guest = User.builder()
                                .email("guest@example.com")
                                .password("password123")
                                .firstName("John")
                                .lastName("Doe")
                                .role(Role.GUEST)
                                .build();
                userRepository.save(guest);

                // Hotel
                Hotel hotel = Hotel.builder()
                                .name("Flow Hotel")
                                .owner(owner)
                                .starRating(4)
                                .location(Location.builder().city("Test City").country("Test").address("123 St")
                                                .build())
                                .cancellationPolicy(CancellationPolicy.builder().name("Flex").build())
                                .build();
                hotelRepository.save(hotel);

                // Room Type
                RoomType roomType = RoomType.builder()
                                .name("Deluxe")
                                .basePrice(new BigDecimal("100.00"))
                                .maxOccupancy(2)
                                .hotel(hotel)
                                .build();
                roomTypeRepository.save(roomType);

                // Room
                Room room = Room.builder()
                                .roomNumber("101")
                                .roomType(roomType)
                                .status(RoomStatus.AVAILABLE)
                                .build();
                roomRepository.save(room);

                // 2. Perform Booking
                ReservationRequest request = ReservationRequest.builder()
                                .roomId(room.getId())
                                .checkInDate(LocalDate.now().plusDays(1))
                                .checkOutDate(LocalDate.now().plusDays(3))
                                .guestCount(2)
                                .build();

                mockMvc.perform(post("/api/reservations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", notNullValue()))
                                .andExpect(jsonPath("$.status").value("PENDING")); // Assuming default status
        }
}

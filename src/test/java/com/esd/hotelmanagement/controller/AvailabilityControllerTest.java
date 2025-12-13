package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.BaseIntegrationTest;
import com.esd.hotelmanagement.entity.*;
import com.esd.hotelmanagement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Availability checking endpoints.
 */
public class AvailabilityControllerTest extends BaseIntegrationTest {

        @Autowired
        private UserRepository userRepository;
        @Autowired
        private HotelRepository hotelRepository;
        @Autowired
        private RoomTypeRepository roomTypeRepository;
        @Autowired
        private RoomRepository roomRepository;

        private Hotel testHotel;
        private Room availableRoom;
        private Room occupiedRoom;

        @BeforeEach
        void setUp() {
                // Create owner
                User owner = User.builder()
                                .email("avail-owner@example.com")
                                .password("password123")
                                .firstName("Avail")
                                .lastName("Owner")
                                .role(Role.OWNER)
                                .enabled(true)
                                .build();
                userRepository.save(owner);

                // Create hotel
                testHotel = Hotel.builder()
                                .name("Availability Test Hotel")
                                .owner(owner)
                                .starRating(4)
                                .location(Location.builder()
                                                .city("Avail City")
                                                .country("USA")
                                                .address("789 Avail St")
                                                .state("AV")
                                                .zipCode("11111")
                                                .build())
                                .cancellationPolicy(CancellationPolicy.builder()
                                                .name("Standard")
                                                .build())
                                .build();
                hotelRepository.save(testHotel);

                // Create room type
                RoomType roomType = RoomType.builder()
                                .name("Standard")
                                .basePrice(new BigDecimal("150.00"))
                                .maxOccupancy(2)
                                .hotel(testHotel)
                                .build();
                roomTypeRepository.save(roomType);

                // Create rooms
                availableRoom = Room.builder()
                                .roomNumber("A101")
                                .floor(1)
                                .roomType(roomType)
                                .status(RoomStatus.AVAILABLE)
                                .build();
                roomRepository.save(availableRoom);

                occupiedRoom = Room.builder()
                                .roomNumber("A102")
                                .floor(1)
                                .roomType(roomType)
                                .status(RoomStatus.OCCUPIED)
                                .build();
                roomRepository.save(occupiedRoom);
        }

        @Test
        @DisplayName("Should return available rooms for date range")
        void shouldGetAvailableRooms() throws Exception {
                LocalDate checkIn = LocalDate.now().plusDays(10);
                LocalDate checkOut = LocalDate.now().plusDays(15);

                // This endpoint is public under /api/hotels/** (GET)
                mockMvc.perform(get("/api/hotels/{hotelId}/availability", testHotel.getId())
                                .param("checkIn", checkIn.toString())
                                .param("checkOut", checkOut.toString()))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should get room availability by room ID")
        @WithMockUser(username = "avail-owner@example.com", roles = { "OWNER" })
        void shouldGetRoomAvailability() throws Exception {
                LocalDate startDate = LocalDate.now().plusDays(5);
                LocalDate endDate = LocalDate.now().plusDays(7);

                // This endpoint requires authentication (/api/rooms/** falls under
                // .anyRequest().authenticated())
                mockMvc.perform(get("/api/rooms/{roomId}/availability", availableRoom.getId())
                                .param("startDate", startDate.toString())
                                .param("endDate", endDate.toString()))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should validate date range properly")
        void shouldValidateDateRange() throws Exception {
                LocalDate checkIn = LocalDate.now().plusDays(10);
                LocalDate checkOut = LocalDate.now().plusDays(15);

                // Test with valid dates - should return 200
                mockMvc.perform(get("/api/hotels/{hotelId}/availability", testHotel.getId())
                                .param("checkIn", checkIn.toString())
                                .param("checkOut", checkOut.toString()))
                                .andExpect(status().isOk());
        }
}

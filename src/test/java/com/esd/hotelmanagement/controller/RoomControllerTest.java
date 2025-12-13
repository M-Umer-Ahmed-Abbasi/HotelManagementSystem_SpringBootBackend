package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.BaseIntegrationTest;
import com.esd.hotelmanagement.dto.RoomRequest;
import com.esd.hotelmanagement.dto.RoomTypeRequest;
import com.esd.hotelmanagement.entity.*;
import com.esd.hotelmanagement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Room and RoomType endpoints.
 */
public class RoomControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    @Autowired
    private RoomRepository roomRepository;

    private Hotel testHotel;
    private User owner;

    @BeforeEach
    void setUp() {
        // Create owner
        owner = User.builder()
                .email("room-test-owner@example.com")
                .password("password123")
                .firstName("Room")
                .lastName("Owner")
                .role(Role.OWNER)
                .enabled(true)
                .build();
        userRepository.save(owner);

        // Create hotel
        testHotel = Hotel.builder()
                .name("Room Test Hotel")
                .owner(owner)
                .starRating(4)
                .location(Location.builder()
                        .city("Test City")
                        .country("USA")
                        .address("123 Test St")
                        .state("TS")
                        .zipCode("12345")
                        .build())
                .cancellationPolicy(CancellationPolicy.builder()
                        .name("Flexible")
                        .build())
                .build();
        hotelRepository.save(testHotel);
    }

    // ==================== Room Type Tests ====================

    @Test
    @DisplayName("Should create room type successfully")
    @WithMockUser(username = "room-test-owner@example.com", roles = { "OWNER" })
    void shouldCreateRoomType() throws Exception {
        RoomTypeRequest request = RoomTypeRequest.builder()
                .name("Deluxe Suite")
                .description("Spacious room with city view")
                .basePrice(new BigDecimal("199.99"))
                .maxOccupancy(4)
                .build();

        mockMvc.perform(post("/api/hotels/{hotelId}/room-types", testHotel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Deluxe Suite")))
                .andExpect(jsonPath("$.basePrice", is(199.99)))
                .andExpect(jsonPath("$.maxOccupancy", is(4)));
    }

    @Test
    @DisplayName("Should get all room types for a hotel")
    @WithMockUser(username = "room-test-owner@example.com", roles = { "OWNER" })
    void shouldGetRoomTypesByHotel() throws Exception {
        // Create room types first
        RoomType type1 = RoomType.builder()
                .name("Standard")
                .basePrice(new BigDecimal("99.99"))
                .maxOccupancy(2)
                .hotel(testHotel)
                .build();
        RoomType type2 = RoomType.builder()
                .name("Premium")
                .basePrice(new BigDecimal("149.99"))
                .maxOccupancy(3)
                .hotel(testHotel)
                .build();
        roomTypeRepository.save(type1);
        roomTypeRepository.save(type2);

        mockMvc.perform(get("/api/hotels/{hotelId}/room-types", testHotel.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Standard", "Premium")));
    }

    @Test
    @DisplayName("Should update room type")
    @WithMockUser(username = "room-test-owner@example.com", roles = { "OWNER" })
    void shouldUpdateRoomType() throws Exception {
        RoomType roomType = RoomType.builder()
                .name("Old Name")
                .basePrice(new BigDecimal("100.00"))
                .maxOccupancy(2)
                .hotel(testHotel)
                .build();
        roomTypeRepository.save(roomType);

        RoomTypeRequest updateRequest = RoomTypeRequest.builder()
                .name("Updated Suite")
                .description("Updated description")
                .basePrice(new BigDecimal("250.00"))
                .maxOccupancy(5)
                .build();

        mockMvc.perform(put("/api/room-types/{id}", roomType.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Suite")))
                .andExpect(jsonPath("$.basePrice", is(250.00)));
    }

    // ==================== Room Tests ====================

    @Test
    @DisplayName("Should create room successfully")
    @WithMockUser(username = "room-test-owner@example.com", roles = { "OWNER" })
    void shouldCreateRoom() throws Exception {
        RoomType roomType = RoomType.builder()
                .name("Test Type")
                .basePrice(new BigDecimal("100.00"))
                .maxOccupancy(2)
                .hotel(testHotel)
                .build();
        roomTypeRepository.save(roomType);

        RoomRequest request = RoomRequest.builder()
                .roomNumber("101")
                .floor(1)
                .status(RoomStatus.AVAILABLE)
                .build();

        mockMvc.perform(post("/api/room-types/{roomTypeId}/rooms", roomType.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomNumber", is("101")))
                .andExpect(jsonPath("$.floor", is(1)))
                .andExpect(jsonPath("$.status", is("AVAILABLE")));
    }

    @Test
    @DisplayName("Should get all rooms for a hotel")
    @WithMockUser(username = "room-test-owner@example.com", roles = { "OWNER" })
    void shouldGetRoomsByHotel() throws Exception {
        RoomType roomType = RoomType.builder()
                .name("Standard")
                .basePrice(new BigDecimal("100.00"))
                .maxOccupancy(2)
                .hotel(testHotel)
                .build();
        roomTypeRepository.save(roomType);

        Room room1 = Room.builder()
                .roomNumber("201")
                .floor(2)
                .roomType(roomType)
                .status(RoomStatus.AVAILABLE)
                .build();
        Room room2 = Room.builder()
                .roomNumber("202")
                .floor(2)
                .roomType(roomType)
                .status(RoomStatus.OCCUPIED)
                .build();
        roomRepository.save(room1);
        roomRepository.save(room2);

        mockMvc.perform(get("/api/hotels/{hotelId}/rooms", testHotel.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].roomNumber", containsInAnyOrder("201", "202")));
    }

    @Test
    @DisplayName("Should update room status")
    @WithMockUser(username = "room-test-owner@example.com", roles = { "OWNER" })
    void shouldUpdateRoomStatus() throws Exception {
        RoomType roomType = RoomType.builder()
                .name("Standard")
                .basePrice(new BigDecimal("100.00"))
                .maxOccupancy(2)
                .hotel(testHotel)
                .build();
        roomTypeRepository.save(roomType);

        Room room = Room.builder()
                .roomNumber("301")
                .floor(3)
                .roomType(roomType)
                .status(RoomStatus.AVAILABLE)
                .build();
        roomRepository.save(room);

        mockMvc.perform(put("/api/rooms/{id}/status", room.getId())
                .param("status", "MAINTENANCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("MAINTENANCE")));
    }

    @Test
    @DisplayName("Should delete room")
    @WithMockUser(username = "room-test-owner@example.com", roles = { "OWNER" })
    void shouldDeleteRoom() throws Exception {
        RoomType roomType = RoomType.builder()
                .name("Standard")
                .basePrice(new BigDecimal("100.00"))
                .maxOccupancy(2)
                .hotel(testHotel)
                .build();
        roomTypeRepository.save(roomType);

        Room room = Room.builder()
                .roomNumber("401")
                .floor(4)
                .roomType(roomType)
                .status(RoomStatus.AVAILABLE)
                .build();
        roomRepository.save(room);

        mockMvc.perform(delete("/api/rooms/{id}", room.getId()))
                .andExpect(status().isNoContent());
    }
}

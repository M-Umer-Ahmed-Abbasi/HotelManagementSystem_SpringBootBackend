package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.BaseIntegrationTest;
import com.esd.hotelmanagement.dto.CancellationPolicyRequest;
import com.esd.hotelmanagement.dto.HotelRequest;
import com.esd.hotelmanagement.dto.LocationRequest;
import com.esd.hotelmanagement.entity.Role;
import com.esd.hotelmanagement.entity.User;
import com.esd.hotelmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class HotelControllerTest extends BaseIntegrationTest {

        @Autowired
        private UserRepository userRepository;

        @BeforeEach
        void setUp() {
                // Create the owner user that matches the @WithMockUser username
                User owner = User.builder()
                                .email("hotel-owner@example.com")
                                .password("password123")
                                .firstName("Hotel")
                                .lastName("Owner")
                                .role(Role.OWNER)
                                .enabled(true)
                                .build();
                userRepository.save(owner);
        }

        @Test
        @DisplayName("Should create hotel when authorized as OWNER")
        @WithMockUser(username = "hotel-owner@example.com", roles = { "OWNER" })
        void shouldCreateHotelAsOwner() throws Exception {
                HotelRequest request = HotelRequest.builder()
                                .name("Grand Hotel")
                                .description("Luxury stay")
                                .starRating(5)
                                .location(LocationRequest.builder()
                                                .city("New York")
                                                .country("USA")
                                                .address("123 Broadway")
                                                .state("NY")
                                                .zipCode("10001")
                                                .build())
                                .cancellationPolicy(CancellationPolicyRequest.builder()
                                                .name("Standard")
                                                .refundPercentage(100)
                                                .daysBeforeCheckIn(2)
                                                .build())
                                .build();

                mockMvc.perform(post("/api/hotels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.name", is("Grand Hotel")))
                                .andExpect(jsonPath("$.id", notNullValue()));
        }

        @Test
        @DisplayName("Should fail to create hotel when unauthorized")
        void shouldFailCreateHotelUnauthorized() throws Exception {
                HotelRequest request = HotelRequest.builder().name("Fail Hotel").build();

                mockMvc.perform(post("/api/hotels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden()); // 403 Forbidden (or 401 if strict)
        }

        @Test
        @DisplayName("Should search hotels publicly")
        void shouldSearchHotels() throws Exception {
                mockMvc.perform(get("/api/hotels")
                                .param("city", "New York"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content", is(instanceOf(java.util.List.class))));
        }
}

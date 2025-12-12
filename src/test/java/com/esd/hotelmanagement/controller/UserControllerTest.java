package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.BaseIntegrationTest;
import com.esd.hotelmanagement.dto.UserUpdateRequest;
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

/**
 * Integration tests for User profile endpoints.
 */
public class UserControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("user-test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phone("+1234567890")
                .role(Role.GUEST)
                .enabled(true)
                .build();
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("Should get current user profile")
    @WithMockUser(username = "user-test@example.com", roles = { "GUEST" })
    void shouldGetCurrentUserProfile() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("user-test@example.com")))
                .andExpect(jsonPath("$.firstName", is("Test")))
                .andExpect(jsonPath("$.lastName", is("User")));
    }

    @Test
    @DisplayName("Should get user by ID")
    @WithMockUser(username = "user-test@example.com", roles = { "GUEST" })
    void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.email", is("user-test@example.com")));
    }

    @Test
    @DisplayName("Should update user profile")
    @WithMockUser(username = "user-test@example.com", roles = { "GUEST" })
    void shouldUpdateUserProfile() throws Exception {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .firstName("Updated")
                .lastName("Name")
                .phone("+9999999999")
                .build();

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Updated")))
                .andExpect(jsonPath("$.lastName", is("Name")))
                .andExpect(jsonPath("$.phone", is("+9999999999")));
    }

    @Test
    @DisplayName("Should fail to access user endpoints without authentication")
    void shouldFailWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should handle user not found")
    @WithMockUser(username = "user-test@example.com", roles = { "GUEST" })
    void shouldHandleUserNotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 99999L))
                .andExpect(status().isNotFound());
    }
}

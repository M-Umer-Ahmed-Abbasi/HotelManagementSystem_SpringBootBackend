package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.BaseIntegrationTest;
import com.esd.hotelmanagement.dto.LoginRequest;
import com.esd.hotelmanagement.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends BaseIntegrationTest {

        @Test
        @DisplayName("Should successfully register a new user")
        void shouldRegisterNewUser() throws Exception {
                RegisterRequest request = RegisterRequest.builder()
                                .email("test.user@example.com")
                                .password("password123")
                                .firstName("Test")
                                .lastName("User")
                                .phone("1234567890")
                                .build();

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.token", notNullValue()))
                                .andExpect(jsonPath("$.email").value("test.user@example.com"));
        }

        @Test
        @DisplayName("Should login successfully with registered user")
        void shouldLoginSuccessfully() throws Exception {
                // First register
                RegisterRequest register = RegisterRequest.builder()
                                .email("login.user@example.com")
                                .password("password123")
                                .firstName("Login")
                                .lastName("User")
                                .build();

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(register)));

                // Then login
                LoginRequest login = new LoginRequest("login.user@example.com", "password123");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(login)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token", notNullValue()));
        }

        @Test
        @DisplayName("Should fail login with wrong password")
        void shouldFailLoginWrongPassword() throws Exception {
                // Register
                RegisterRequest register = RegisterRequest.builder()
                                .email("wrong.pass@example.com")
                                .password("password123")
                                .firstName("User")
                                .lastName("One")
                                .build();

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(register)));

                // Login wrong password
                LoginRequest login = new LoginRequest("wrong.pass@example.com", "wrongpassword");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(login)))
                                .andExpect(status().isUnauthorized()); // 401 for bad credentials
        }
}

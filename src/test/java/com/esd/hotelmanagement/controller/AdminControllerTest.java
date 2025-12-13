package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Should allow ADMIN to list users")
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void shouldAllowAdminToListUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should deny GUEST from listing users")
    @WithMockUser(username = "guest", roles = { "GUEST" })
    void shouldDenyGuestFromListingUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }
}

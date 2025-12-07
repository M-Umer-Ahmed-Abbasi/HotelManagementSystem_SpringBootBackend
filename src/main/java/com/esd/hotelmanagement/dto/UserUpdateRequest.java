package com.esd.hotelmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request DTO for updating user profile.
 * 
 * @author Umer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String phone;
}

package com.esd.hotelmanagement.dto;

import com.esd.hotelmanagement.entity.Role;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for user information.
 * 
 * @author Umer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
    private Boolean enabled;
    private LocalDateTime createdAt;
}

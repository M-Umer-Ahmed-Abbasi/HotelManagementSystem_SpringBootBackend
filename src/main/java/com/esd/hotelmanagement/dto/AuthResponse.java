package com.esd.hotelmanagement.dto;

import com.esd.hotelmanagement.entity.Role;
import lombok.*;

/**
 * Response DTO for authentication (login/register).
 * 
 * @author Umer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}

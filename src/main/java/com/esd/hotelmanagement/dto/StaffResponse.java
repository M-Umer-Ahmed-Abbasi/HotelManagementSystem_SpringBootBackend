package com.esd.hotelmanagement.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for staff information.
 * 
 * @author Talha
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffResponse {

    private Long id;
    private String position;
    private LocalDateTime hireDate;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long hotelId;
    private String hotelName;
}

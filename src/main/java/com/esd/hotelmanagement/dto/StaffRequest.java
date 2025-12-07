package com.esd.hotelmanagement.dto;

import lombok.*;

/**
 * Request DTO for assigning staff to hotel.
 * 
 * @author Talha
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffRequest {

    private Long userId;
    private String position;
}

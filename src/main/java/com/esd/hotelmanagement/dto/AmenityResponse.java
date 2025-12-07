package com.esd.hotelmanagement.dto;

import lombok.*;

/**
 * Response DTO for amenity information.
 * 
 * @author Umer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmenityResponse {

    private Long id;
    private String name;
    private String description;
    private String icon;
}

package com.esd.hotelmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request DTO for creating amenities.
 * 
 * @author Umer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmenityRequest {

    @NotBlank(message = "Amenity name is required")
    private String name;

    private String description;

    private String icon;
}

package com.esd.hotelmanagement.dto;

import lombok.*;

/**
 * Response DTO for location information.
 * 
 * @author Umer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationResponse {

    private Long id;
    private String city;
    private String state;
    private String country;
    private String address;
    private String zipCode;
    private Double latitude;
    private Double longitude;
}

package com.esd.hotelmanagement.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Response DTO for room type information.
 * 
 * @author Umer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Integer maxOccupancy;
    private Long hotelId;
    private String hotelName;
    private Integer roomCount;
    private Integer availableRoomCount;
}

package com.esd.hotelmanagement.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for hotel information.
 * 
 * @author Umer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse {

    private Long id;
    private String name;
    private String description;
    private Integer starRating;
    private Boolean enabled;
    private LocalDateTime createdAt;

    // Owner info
    private Long ownerId;
    private String ownerName;

    // Location
    private LocationResponse location;

    // Amenities
    private List<AmenityResponse> amenities;

    // Photos
    private List<PhotoResponse> photos;

    // Average rating from reviews
    private Double averageRating;
    private Integer reviewCount;

    // Room type summary
    private Integer roomTypeCount;
    private Integer totalRooms;
}
